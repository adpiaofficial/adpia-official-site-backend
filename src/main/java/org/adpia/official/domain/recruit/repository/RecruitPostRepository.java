package org.adpia.official.domain.recruit.repository;

import java.util.Optional;

import org.adpia.official.domain.recruit.RecruitBoardCode;
import org.adpia.official.domain.recruit.RecruitPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecruitPostRepository extends JpaRepository<RecruitPost, Long> {

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
    update RecruitPost p
       set p.viewCount = p.viewCount + 1
     where p.id = :postId
       and p.deletedAt is null
""")
	int incrementViewCount(@Param("postId") Long postId);


	Optional<RecruitPost> findByIdAndDeletedAtIsNull(Long id);

	Page<RecruitPost> findByBoardCodeAndDeletedAtIsNullOrderByPinnedDescPinnedAtDescCreatedAtDesc(
		RecruitBoardCode boardCode, Pageable pageable
	);
}
