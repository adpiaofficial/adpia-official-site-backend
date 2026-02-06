package org.adpia.official.domain.executive.controller;

import lombok.RequiredArgsConstructor;
import org.adpia.official.domain.executive.service.ExecutiveService;
import org.adpia.official.dto.executive.ExecutiveGroupResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/executives")
public class ExecutivePublicController {

	private final ExecutiveService executiveService;

	@GetMapping
	public List<ExecutiveGroupResponse> getExecutives() {
		return executiveService.getPublicExecutives();
	}
}
