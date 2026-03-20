package org.adpia.official.domain.recruit.service;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.adpia.official.domain.recruit.*;
import org.adpia.official.domain.recruit.repository.RecruitCommentLikeRepository;
import org.adpia.official.domain.recruit.repository.RecruitCommentRepository;
import org.adpia.official.domain.recruit.repository.RecruitPostLikeRepository;
import org.adpia.official.domain.recruit.repository.RecruitPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecruitLikeService {

	private final RecruitPostRepository postRepository;
	private final RecruitCommentRepository commentRepository;
	private final RecruitPostLikeRepository postLikeRepository;
	private final RecruitCommentLikeRepository commentLikeRepository;

	@Transactional
	public void likePost(Long postId, RecruitService.Actor actor) {
		if (actor.isGuest()) {
			throw new IllegalStateException("좋아요는 로그인 후 사용할 수 있습니다.");
		}

		RecruitPost post = postRepository.findByIdAndDeletedAtIsNull(postId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

		if (!post.isLikeEnabled()) {
			throw new IllegalStateException("좋아요가 비활성화된 게시글입니다.");
		}

		boolean exists = postLikeRepository.existsByPostIdAndMemberId(postId, actor.memberId());
		if (exists) return;

		postLikeRepository.save(
			RecruitPostLike.builder()
				.postId(postId)
				.memberId(actor.memberId())
				.createdAt(LocalDateTime.now())
				.build()
		);

		post.setLikeCount(post.getLikeCount() + 1);
	}

	@Transactional
	public void unlikePost(Long postId, RecruitService.Actor actor) {
		if (actor.isGuest()) {
			throw new IllegalStateException("좋아요는 로그인 후 사용할 수 있습니다.");
		}

		RecruitPost post = postRepository.findByIdAndDeletedAtIsNull(postId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

		boolean exists = postLikeRepository.existsByPostIdAndMemberId(postId, actor.memberId());
		if (!exists) return;

		postLikeRepository.deleteByPostIdAndMemberId(postId, actor.memberId());
		post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
	}

	@Transactional
	public void likeComment(Long commentId, RecruitService.Actor actor) {
		if (actor.isGuest()) {
			throw new IllegalStateException("좋아요는 로그인 후 사용할 수 있습니다.");
		}

		RecruitComment comment = commentRepository.findByIdAndDeletedFalse(commentId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

		RecruitPost post = postRepository.findByIdAndDeletedAtIsNull(comment.getPostId())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

		if (!post.isCommentEnabled()) {
			throw new IllegalStateException("댓글이 비활성화된 게시글입니다.");
		}
		if (!post.isLikeEnabled()) {
			throw new IllegalStateException("좋아요가 비활성화된 게시글입니다.");
		}

		boolean exists = commentLikeRepository.existsByCommentIdAndMemberId(commentId, actor.memberId());
		if (exists) return;

		commentLikeRepository.save(
			RecruitCommentLike.builder()
				.commentId(commentId)
				.memberId(actor.memberId())
				.createdAt(LocalDateTime.now())
				.build()
		);

		comment.setLikeCount(comment.getLikeCount() + 1);
	}

	@Transactional
	public void unlikeComment(Long commentId, RecruitService.Actor actor) {
		if (actor.isGuest()) {
			throw new IllegalStateException("좋아요는 로그인 후 사용할 수 있습니다.");
		}

		RecruitComment comment = commentRepository.findByIdAndDeletedFalse(commentId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

		boolean exists = commentLikeRepository.existsByCommentIdAndMemberId(commentId, actor.memberId());
		if (!exists) return;

		commentLikeRepository.deleteByCommentIdAndMemberId(commentId, actor.memberId());
		comment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
	}
}