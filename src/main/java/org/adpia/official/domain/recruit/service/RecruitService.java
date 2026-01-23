package org.adpia.official.domain.recruit.service;

import java.time.LocalDateTime;
import java.util.List;

import org.adpia.official.domain.recruit.*;
import org.adpia.official.dto.recruit.*;
import org.adpia.official.domain.recruit.repository.*;
import org.adpia.official.domain.member.MemberRole;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecruitService {

	private final RecruitPostRepository postRepository;
	private final RecruitPostBlockRepository blockRepository;
	private final PasswordEncoder passwordEncoder;
	private final RecruitPostBlockRepository recruitPostBlockRepository;

	@Transactional
	public RecruitPostResponse create(RecruitBoardCode boardCode,
		RecruitPostUpsertRequest req,
		Actor actor // 아래에 간단 Actor 클래스 제공
	) {

		validateCreatePermission(boardCode, actor);

		boolean isGuest = actor.isGuest();

		RecruitPost post = RecruitPost.builder()
			.boardCode(boardCode)
			.title(req.getTitle())
			.authorType(isGuest ? RecruitAuthorType.GUEST : RecruitAuthorType.MEMBER)
			.authorMemberId(isGuest ? null : actor.memberId())
			.authorName(isGuest ? requireGuestName(req.getAuthorName()) : actor.displayName())
			.secret(Boolean.TRUE.equals(req.getSecret()))
			.commentEnabled(boardCode == RecruitBoardCode.QA)
			.likeEnabled(true)
			.pinned(false)
			.viewCount(0)
			.build();

		if (post.isSecret()) {
			String rawPw = requirePassword(req.getPassword());
			post.setSecretPasswordHash(passwordEncoder.encode(rawPw));
		} else {
			post.setSecretPasswordHash(null);
		}

		if (boardCode == RecruitBoardCode.NOTICE) {
			post.setCommentEnabled(false);
		}

		RecruitPost saved = postRepository.save(post);

		List<RecruitBlockRequest> blocks = req.getBlocks() == null ? List.of() : req.getBlocks();
		saveBlocks(saved.getId(), blocks);

		return get(saved.getId());
	}

	@Transactional
	public RecruitPostResponse get(Long postId) {

		postRepository.incrementViewCount(postId);

		RecruitPost post = postRepository.findByIdAndDeletedAtIsNull(postId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

		List<RecruitBlockResponse> blocks = blockRepository.findByPostIdOrderBySortOrderAsc(postId)
			.stream().map(RecruitBlockResponse::from).toList();

		return RecruitPostResponse.from(post, blocks);
	}

	@Transactional(readOnly = true)
	public Page<RecruitPostResponse> list(RecruitBoardCode boardCode, Pageable pageable) {
		Page<RecruitPost> page = postRepository
			.findByBoardCodeAndDeletedAtIsNullOrderByPinnedDescPinnedAtDescCreatedAtDesc(boardCode, pageable);

		return page.map(p -> RecruitPostResponse.from(p, List.of()));
	}

	@Transactional
	public RecruitPostResponse update(Long postId, RecruitPostUpsertRequest req, Actor actor, String guestPasswordOrNull) {
		RecruitPost post = postRepository.findByIdAndDeletedAtIsNull(postId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

		validateUpdateDeletePermission(post, actor, guestPasswordOrNull);

		post.setTitle(req.getTitle());

		boolean nextSecret = Boolean.TRUE.equals(req.getSecret());
		post.setSecret(nextSecret);
		if (nextSecret) {
			post.setSecretPasswordHash(passwordEncoder.encode(requirePassword(req.getPassword())));
		} else {
			post.setSecretPasswordHash(null);
		}

		blockRepository.deleteByPostId(postId);
		saveBlocks(postId, req.getBlocks() == null ? List.of() : req.getBlocks());

		return get(postId);
	}

	@Transactional
	public void delete(Long postId, Actor actor, String guestPasswordOrNull) {
		RecruitPost post = postRepository.findByIdAndDeletedAtIsNull(postId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

		System.out.println("actor.memberId=" + actor.memberId()
			+ ", actor.role=" + actor.role()
			+ ", post.authorType=" + post.getAuthorType()
			+ ", post.authorMemberId=" + post.getAuthorMemberId());


		validateUpdateDeletePermission(post, actor, guestPasswordOrNull);

		post.setDeletedAt(LocalDateTime.now());
	}

	/* ------------------ 내부 유틸 ------------------ */

	private void validateCreatePermission(RecruitBoardCode boardCode, Actor actor) {
		if (boardCode == RecruitBoardCode.NOTICE) {
			// 공지 작성: SUPER_ADMIN or PRESIDENT
			if (actor.isGuest()) throw new IllegalStateException("공지사항은 로그인 후 작성할 수 있습니다.");
			if (!(actor.role() == MemberRole.ROLE_SUPER_ADMIN || actor.role() == MemberRole.ROLE_PRESIDENT)) {
				throw new IllegalStateException("공지사항 작성 권한이 없습니다.");
			}
		}
	}

	private void validateUpdateDeletePermission(RecruitPost post, Actor actor, String guestPasswordOrNull) {
		if (!actor.isGuest() && actor.role() == MemberRole.ROLE_SUPER_ADMIN) return;

		if (post.getAuthorType() == RecruitAuthorType.MEMBER) {
			if (actor.isGuest()) throw new IllegalStateException("권한이 없습니다.");
			if (post.getAuthorMemberId() == null || !post.getAuthorMemberId().equals(actor.memberId())) {
				throw new IllegalStateException("본인 글만 수정/삭제할 수 있습니다.");
			}
			return;
		}

		if (post.getAuthorType() == RecruitAuthorType.GUEST) {
			if (!post.isSecret()) {
				throw new IllegalStateException("외부 작성 글은 비밀글로만 수정/삭제할 수 있습니다.");
			}
			String raw = requirePassword(guestPasswordOrNull);
			if (post.getSecretPasswordHash() == null || !passwordEncoder.matches(raw, post.getSecretPasswordHash())) {
				throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
			}
		}
	}

	private void saveBlocks(Long postId, List<RecruitBlockRequest> blocks) {

		recruitPostBlockRepository.deleteByPostId(postId);

		if (blocks == null || blocks.isEmpty()) return;

		for (RecruitBlockRequest b : blocks) {
			validateBlock(b);

			RecruitPostBlock entity = RecruitPostBlock.builder()
				.postId(postId)
				.sortOrder(b.getSortOrder())
				.type(b.getType())
				.text(b.getText())
				.url(b.getUrl())
				.meta(b.getMeta())
				.build();
			blockRepository.save(entity);
		}
	}

	private void validateBlock(RecruitBlockRequest b) {
		if (b.getType() == null) throw new IllegalArgumentException("block type이 필요합니다.");

		if (b.getType() == RecruitBlockType.TEXT) {
			if (b.getText() == null || b.getText().isBlank()) {
				throw new IllegalArgumentException("TEXT 블록은 text가 필요합니다.");
			}
		} else {
			if (b.getUrl() == null || b.getUrl().isBlank()) {
				throw new IllegalArgumentException(b.getType() + " 블록은 url이 필요합니다.");
			}
		}
	}

	private String requirePassword(String pw) {
		if (pw == null || pw.isBlank()) throw new IllegalArgumentException("비밀번호가 필요합니다.");
		return pw;
	}

	private String requireGuestName(String name) {
		if (name == null || name.isBlank()) throw new IllegalArgumentException("작성자 이름이 필요합니다.");
		return name;
	}

	public record Actor(Long memberId, String displayName, MemberRole role) {
		public boolean isGuest() { return memberId == null; }
		public static Actor guest() { return new Actor(null, "GUEST", null); }
	}

	@Transactional
	public void updatePinned(Long postId, boolean pinned, Actor actor) {
		RecruitPost post = postRepository.findByIdAndDeletedAtIsNull(postId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

		if (post.getBoardCode() != RecruitBoardCode.NOTICE) {
			throw new IllegalStateException("공지사항만 고정 설정이 가능합니다.");
		}

		if (actor.isGuest()) throw new IllegalStateException("권한이 없습니다.");
		if (!(actor.role() == MemberRole.ROLE_SUPER_ADMIN || actor.role() == MemberRole.ROLE_PRESIDENT)) {
			throw new IllegalStateException("고정 설정 권한이 없습니다.");
		}

		post.setPinned(pinned);
		post.setPinnedAt(pinned ? LocalDateTime.now() : null);
	}
}
