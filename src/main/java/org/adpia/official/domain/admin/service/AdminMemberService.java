package org.adpia.official.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.adpia.official.domain.member.Member;
import org.adpia.official.domain.member.MemberRole;
import org.adpia.official.domain.member.repository.MemberRepository;
import org.adpia.official.dto.member.MemberResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMemberService {

	private final MemberRepository memberRepository;

	public List<MemberResponse> getMembers() {
		return memberRepository.findAll().stream()
			.map(MemberResponse::from)
			.toList();
	}

	/**
	 * 한글 grade -> MemberRole 매핑
	 */
	private MemberRole resolveRoleFromGrade(String grade) {
		if (grade == null) {
			return MemberRole.ROLE_USER;
		}

		grade = grade.trim();

		return switch (grade) {
			case "활동기수", "OB" -> MemberRole.ROLE_USER;
			case "회장단" -> MemberRole.ROLE_PRESIDENT;
			case "마스터" -> MemberRole.ROLE_SUPER_ADMIN;
			default -> MemberRole.ROLE_USER;
		};
	}

	/**
	 * 회장단 / 활동기수 / OB / 마스터 같은 '표면적 지위' 수정
	 * -> grade를 바꾸면 role도 자동으로 따라옴
	 */
	@Transactional
	public MemberResponse updateGrade(Long memberId, String grade) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. id=" + memberId));

		MemberRole newRole = resolveRoleFromGrade(grade);

		if (newRole == MemberRole.ROLE_SUPER_ADMIN) {
			boolean existsAnotherMaster =
				memberRepository.existsByRole(MemberRole.ROLE_SUPER_ADMIN)
					&& member.getRole() != MemberRole.ROLE_SUPER_ADMIN;

			if (existsAnotherMaster) {
				throw new IllegalStateException("이미 마스터 계정이 존재합니다.");
			}
		}

		member.setGrade(grade);
		member.setRole(newRole);
		member.setUpdatedAt(LocalDateTime.now());

		return MemberResponse.from(member);
	}

	@Transactional
	public MemberResponse updateActive(Long targetMemberId, Boolean active) {
		if (active == null) {
			throw new IllegalArgumentException("active 값이 필요합니다.");
		}

		Member target = memberRepository.findById(targetMemberId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

		if (target.getRole() == MemberRole.ROLE_SUPER_ADMIN) {
			throw new IllegalStateException("마스터 계정은 활성/비활성 상태를 변경할 수 없습니다.");
		}

		target.setActive(active);
		return MemberResponse.from(target);
	}

}
