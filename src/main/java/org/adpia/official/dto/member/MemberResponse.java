package org.adpia.official.dto.member;

import lombok.Builder;
import lombok.Getter;
import org.adpia.official.domain.member.Member;

@Getter
@Builder
public class MemberResponse {
	private Long id;
	private String name;
	private String department;
	private String email;
	private String gender;
	private int generation;
	private String grade;
	private String role;

	public static MemberResponse from(Member member) {
		return MemberResponse.builder()
			.id(member.getId())
			.name(member.getName())
			.department(member.getDepartment())
			.email(member.getEmail())
			.gender(member.getGender())
			.generation(member.getGeneration())
			.grade(member.getGrade())
			.role(member.getRole().name())
			.build();
	}
}
