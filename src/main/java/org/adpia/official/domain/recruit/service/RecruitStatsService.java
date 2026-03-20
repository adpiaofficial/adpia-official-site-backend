package org.adpia.official.domain.recruit.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.adpia.official.domain.member.MemberRole;
import org.adpia.official.domain.recruit.repository.RecruitCommentRepository;
import org.adpia.official.dto.recruit.HundredQnaCommentStatResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecruitStatsService {

	private final RecruitCommentRepository commentRepository;

	@Transactional(readOnly = true)
	public List<HundredQnaCommentStatResponse> getHundredQnaCommentStats(RecruitService.Actor actor) {
		if (actor.isGuest()) {
			throw new IllegalStateException("권한이 없습니다.");
		}
		if (!(actor.role() == MemberRole.ROLE_SUPER_ADMIN || actor.role() == MemberRole.ROLE_PRESIDENT)) {
			throw new IllegalStateException("권한이 없습니다.");
		}

		return commentRepository.findHundredQnaCommentStats()
			.stream()
			.map(row -> HundredQnaCommentStatResponse.builder()
				.memberId(row.getMemberId())
				.memberName(row.getMemberName())
				.department(row.getDepartment())
				.generation(row.getGeneration() == null ? 0 : row.getGeneration())
				.commentCount(row.getCommentCount() == null ? 0 : row.getCommentCount())
				.build())
			.toList();
	}
}