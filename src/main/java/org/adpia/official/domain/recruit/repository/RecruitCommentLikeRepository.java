package org.adpia.official.domain.recruit.repository;

import java.util.Optional;

import org.adpia.official.domain.recruit.RecruitCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruitCommentLikeRepository extends JpaRepository<RecruitCommentLike, Long> {
	Optional<RecruitCommentLike> findByCommentIdAndMemberId(Long commentId, Long memberId);
	boolean existsByCommentIdAndMemberId(Long commentId, Long memberId);
	void deleteByCommentIdAndMemberId(Long commentId, Long memberId);
}