package org.adpia.official.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.adpia.official.dto.member.MemberResponse;
import org.adpia.official.dto.member.UpdateActiveRequest;
import org.adpia.official.dto.member.UpdateGradeRequest;
import org.adpia.official.domain.admin.service.AdminMemberService;
import org.adpia.official.dto.member.UpdateRoleRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

	import java.util.List;

@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

	private final AdminMemberService adminMemberService;

	@GetMapping
	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public ResponseEntity<List<MemberResponse>> getMembers() {
		return ResponseEntity.ok(adminMemberService.getMembers());
	}

	// 회장단, 활동기수, OB같은 표면적 지위 수정
	@PatchMapping("/{id}/grade")
	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public ResponseEntity<MemberResponse> updateMemberGrade(
		@PathVariable Long id,
		@RequestBody UpdateGradeRequest request
	) {
		MemberResponse updated = adminMemberService.updateGrade(id, request.getGrade());
		return ResponseEntity.ok(updated);
	}

	@PatchMapping("/{id}/active")
	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public ResponseEntity<MemberResponse> updateMemberActive(
		@PathVariable Long id,
		@RequestBody UpdateActiveRequest request
	) {
		MemberResponse updated = adminMemberService.updateActive(id, request.getActive());
		return ResponseEntity.ok(updated);
	}

}
