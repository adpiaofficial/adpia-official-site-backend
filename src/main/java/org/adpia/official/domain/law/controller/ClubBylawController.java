package org.adpia.official.domain.law.controller;

import org.adpia.official.domain.law.service.ClubBylawService;
import org.adpia.official.dto.law.BylawResponse;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bylaw")
public class ClubBylawController {

	private final ClubBylawService clubBylawService;

	@GetMapping
	public BylawResponse get() {
		return clubBylawService.getOrNull();
	}
}