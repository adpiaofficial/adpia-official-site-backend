package org.adpia.official.domain.recruit.repository;

import java.util.Optional;

import org.adpia.official.domain.recruit.RecruitPostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruitPostLikeRepository extends JpaRepository<RecruitPostLike, Long> {
	Optional<RecruitPostLike> findByPostIdAndMemberId(Long postId, Long memberId);
	boolean existsByPostIdAndMemberId(Long postId, Long memberId);
	void deleteByPostIdAndMemberId(Long postId, Long memberId);
}