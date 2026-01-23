package org.adpia.official.domain.recruit.repository;

import java.util.List;

import org.adpia.official.domain.recruit.RecruitPostBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecruitPostBlockRepository extends JpaRepository<RecruitPostBlock, Long> {

	@Modifying
	@Query("delete from RecruitPostBlock b where b.postId = :postId")
	void deleteByPostId(@Param("postId") Long postId);

	List<RecruitPostBlock> findByPostIdOrderBySortOrderAsc(Long postId);

}
