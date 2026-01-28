package org.adpia.official.domain.recruit.repository;

import java.util.List;
import java.util.Optional;

import org.adpia.official.domain.recruit.RecruitComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitCommentRepository extends JpaRepository<RecruitComment, Long> {
	List<RecruitComment> findByPostIdOrderByCreatedAtAsc(Long postId);
	Optional<RecruitComment> findByIdAndDeletedFalse(Long id);
}
