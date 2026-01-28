package org.adpia.official.domain.recruit.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.adpia.official.domain.member.MemberRole;
import org.adpia.official.domain.recruit.RecruitAuthorType;
import org.adpia.official.domain.recruit.RecruitBoardCode;
import org.adpia.official.domain.recruit.RecruitPost;
import org.adpia.official.domain.recruit.RecruitPostBlock;
import org.adpia.official.domain.recruit.RecruitBlockType;
import org.adpia.official.domain.recruit.repository.RecruitPostBlockRepository;
import org.adpia.official.domain.recruit.repository.RecruitPostRepository;
import org.adpia.official.dto.recruit.RecruitBlockRequest;
import org.adpia.official.dto.recruit.RecruitBlockResponse;
import org.adpia.official.dto.recruit.RecruitPostResponse;
import org.adpia.official.dto.recruit.RecruitPostUpsertRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	@Transactional
	public RecruitPostResponse create(RecruitBoardCode boardCode,
		RecruitPostUpsertRequest req,
		Actor actor
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
			if (isGuest) {
				String rawPw = requirePassword(req.getPassword());
				post.setSecretPasswordHash(passwordEncoder.encode(rawPw));
			} else {
				if (req.getPassword()!= null && !req.getPassword().isBlank()){
					post.setSecretPasswordHash(passwordEncoder.encode(req.getPassword()));
				}else {
					post.setSecretPasswordHash(null);
				}
			}
		} else {
			post.setSecretPasswordHash(null);
		}

		if (boardCode == RecruitBoardCode.NOTICE) {
			post.setCommentEnabled(false);
		}

		RecruitPost saved = postRepository.save(post);

		List<RecruitBlockRequest> blocks = req.getBlocks() == null ? List.of() : req.getBlocks();
		saveBlocks(saved.getId(), blocks);

		return get(saved.getId(), actor, req.getPassword());
	}

	@Transactional
	public RecruitPostResponse get(Long postId, Actor actor, String passwordOrNull) {
		postRepository.incrementViewCount(postId);

		RecruitPost post = postRepository.findByIdAndDeletedAtIsNull(postId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

		boolean locked = isLockedForRead(post, actor, passwordOrNull);

		List<RecruitBlockResponse> blocks = locked
			? List.of()
			: blockRepository.findByPostIdOrderBySortOrderAsc(postId)
			.stream().map(RecruitBlockResponse::from).toList();

		return RecruitPostResponse.from(post, blocks, locked);
	}

	@Transactional
	public Page<RecruitPostResponse> list(RecruitBoardCode boardCode, Pageable pageable) {
		Page<RecruitPost> page = postRepository
			.findByBoardCodeAndDeletedAtIsNullOrderByPinnedDescPinnedAtDescCreatedAtDesc(boardCode, pageable);

		return page.map(p -> RecruitPostResponse.from(p, List.of(), false));
	}

	@Transactional
	public RecruitPostResponse update(Long postId, RecruitPostUpsertRequest req, Actor actor, String guestPasswordOrNull) {
		RecruitPost post = postRepository.findByIdAndDeletedAtIsNull(postId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

		String guestPw = guestPasswordOrNull;
		if ((guestPw == null || guestPw.isBlank()) && post.getAuthorType() == RecruitAuthorType.GUEST) {
			guestPw = req.getPassword();
		}

		validateUpdateDeletePermission(post, actor, guestPw);

		post.setTitle(req.getTitle());

		boolean nextSecret = Boolean.TRUE.equals(req.getSecret());
		post.setSecret(nextSecret);


		if (!nextSecret) {
			post.setSecretPasswordHash(null);
		} else {
			if (post.getAuthorType() == RecruitAuthorType.GUEST) {
				post.setSecretPasswordHash(passwordEncoder.encode(requirePassword(req.getPassword())));
			} else {
				if (req.getPassword() != null && !req.getPassword().isBlank()) {
					post.setSecretPasswordHash(passwordEncoder.encode(req.getPassword().trim()));
				} else {
					post.setSecretPasswordHash(null);
				}
			}
		}

		blockRepository.deleteByPostId(postId);
		saveBlocks(postId, req.getBlocks() == null ? List.of() : req.getBlocks());

		return get(postId, actor, req.getPassword());
	}

	@Transactional
	public void delete(Long postId, Actor actor, String guestPasswordOrNull) {
		RecruitPost post = postRepository.findByIdAndDeletedAtIsNull(postId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

		validateUpdateDeletePermission(post, actor, guestPasswordOrNull);

		post.setDeletedAt(LocalDateTime.now());
	}

	@Transactional
	public void updatePinned(Long postId, boolean pinned, Actor actor) {
		RecruitPost post = postRepository.findByIdAndDeletedAtIsNull(postId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

		if (!(post.getBoardCode() == RecruitBoardCode.NOTICE || post.getBoardCode() == RecruitBoardCode.QA)) {
			throw new IllegalStateException("고정 설정이 불가능한 게시판입니다.");
		}

		if (actor.isGuest()) throw new IllegalStateException("권한이 없습니다.");
		if (!(actor.role() == MemberRole.ROLE_SUPER_ADMIN || actor.role() == MemberRole.ROLE_PRESIDENT)) {
			throw new IllegalStateException("고정 설정 권한이 없습니다.");
		}

		post.setPinned(pinned);
		post.setPinnedAt(pinned ? LocalDateTime.now() : null);
	}

	private void validateCreatePermission(RecruitBoardCode boardCode, Actor actor) {
		if (boardCode == RecruitBoardCode.QA) return;

		if (actor.isGuest()) throw new IllegalStateException("공지사항은 로그인 후 작성할 수 있습니다.");
		if (!(actor.role() == MemberRole.ROLE_SUPER_ADMIN || actor.role() == MemberRole.ROLE_PRESIDENT)) {
			throw new IllegalStateException("공지사항 작성 권한이 없습니다.");
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

	private boolean isLockedForRead(RecruitPost post, Actor actor, String passwordOrNull) {
		if (!post.isSecret()) return false;

		// 운영진은 항상 열람 가능
		if (!actor.isGuest() &&
			(actor.role() == MemberRole.ROLE_SUPER_ADMIN || actor.role() == MemberRole.ROLE_PRESIDENT)) {
			return false;
		}

		// 로그인 작성자 본인은 열람 가능
		if (post.getAuthorType() == RecruitAuthorType.MEMBER
			&& !actor.isGuest()
			&& Objects.equals(post.getAuthorMemberId(), actor.memberId())) {
			return false;
		}

		// 게스트 글은 비번으로 열람
		if (post.getAuthorType() == RecruitAuthorType.GUEST) {
			if (passwordOrNull == null || passwordOrNull.isBlank()) return true;
			String hash = post.getSecretPasswordHash();
			if (hash == null || hash.isBlank()) return true;
			return !passwordEncoder.matches(passwordOrNull, hash);
		}

		// MEMBER 비밀글인데 작성자/운영진이 아니면 잠금
		return true;
	}

	private void saveBlocks(Long postId, List<RecruitBlockRequest> blocks) {
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
}
