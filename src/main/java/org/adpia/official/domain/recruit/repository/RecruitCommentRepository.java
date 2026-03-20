package org.adpia.official.domain.recruit.repository;

import java.util.List;
import java.util.Optional;

import org.adpia.official.domain.recruit.RecruitComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecruitCommentRepository extends JpaRepository<RecruitComment, Long> {

	List<RecruitComment> findByPostIdOrderByCreatedAtAsc(Long postId);

	Optional<RecruitComment> findByIdAndDeletedFalse(Long id);

	interface HundredQnaCommentStatProjection {
		Long getMemberId();
		String getMemberName();
		String getDepartment();
		Integer getGeneration();
		Long getCommentCount();
	}

	@Query(value = """
		select
			rc.author_member_id as memberId,
			m.name as memberName,
			m.department as department,
			m.generation as generation,
			count(*) as commentCount
		from recruit_comments rc
		join recruit_posts rp on rp.id = rc.post_id
		join members m on m.id = rc.author_member_id
		where rp.board_code = 'HUNDRED_QNA'
		  and rc.deleted = false
		  and rc.author_type = 'MEMBER'
		  and rc.author_member_id is not null
		  and rp.deleted_at is null
		  and rp.status = 'PUBLISHED'
		group by rc.author_member_id, m.name, m.department, m.generation
		order by count(*) desc, m.generation asc, m.name asc
		""", nativeQuery = true)
	List<HundredQnaCommentStatProjection> findHundredQnaCommentStats();
}