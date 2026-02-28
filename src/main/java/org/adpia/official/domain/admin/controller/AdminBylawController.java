package org.adpia.official.domain.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.adpia.official.domain.law.service.ClubBylawService;
import org.adpia.official.dto.law.BylawResponse;
import org.adpia.official.dto.law.BylawUpsertRequest;
import org.adpia.official.security.MemberPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/bylaw")
public class AdminBylawController {

	private final ClubBylawService ClubBylawService;

	@PutMapping
	public BylawResponse upsert(
		@Valid @RequestBody BylawUpsertRequest req,
		@AuthenticationPrincipal MemberPrincipal me
	) {
		String updatedBy = (me != null) ? me.getUsername() : "ADMIN";
		return ClubBylawService.upsert(req.getContent(), updatedBy);
	}
}