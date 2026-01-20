package org.adpia.official.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.adpia.official.domain.member.Member;
import org.adpia.official.domain.member.MemberRole;
import org.adpia.official.domain.member.repository.MemberRepository;
import org.adpia.official.dto.member.MemberResponse;
import org.adpia.official.dto.member.SignupRequest;
import org.adpia.official.dto.member.UpdateProfileRequest;
import org.adpia.official.security.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public void signup(SignupRequest req) {
		if (memberRepository.existsByEmail(req.getEmail())) {
			throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
		}

		Member member = Member.builder()
			.name(req.getName())
			.department(req.getDepartment())
			.email(req.getEmail())
			.password(passwordEncoder.encode(req.getPassword()))
			.gender(req.getGender())
			.generation(req.getGeneration())
			.grade(null)
			.role(MemberRole.ROLE_USER)
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.active(true)
			.build();

		memberRepository.save(member);
	}

	@Transactional(readOnly = true)
	public MemberResponse getCurrentMember() {
		String email = SecurityUtil.getCurrentUserEmail();
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
		return MemberResponse.from(member);
	}

	@Transactional
	public MemberResponse updateProfile(UpdateProfileRequest req) {
		String email = SecurityUtil.getCurrentUserEmail();
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		if (req.getName() != null) member.setName(req.getName());
		if (req.getDepartment() != null) member.setDepartment(req.getDepartment());
		if (req.getGender() != null) member.setGender(req.getGender());
		if (req.getGeneration() != null) member.setGeneration(req.getGeneration());

		member.setUpdatedAt(LocalDateTime.now());

		return MemberResponse.from(member);
	}

	@Transactional
	public MemberResponse updateGrade(Long memberId, String grade) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

		member.setGrade(grade);
		member.setUpdatedAt(LocalDateTime.now());

		return MemberResponse.from(member);
	}

	@Transactional
	public Member getMemberByEmail(String email) {
		return memberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. email=" + email));
	}


}
