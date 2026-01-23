package org.adpia.official.domain.recruit.controller;

import org.adpia.official.domain.recruit.RecruitBoardCode;
import org.adpia.official.dto.recruit.*;
import org.adpia.official.domain.recruit.service.RecruitService;
import org.adpia.official.domain.recruit.service.RecruitService.Actor;
import org.adpia.official.domain.member.MemberRole;
import org.adpia.official.security.ActorResolver;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruit")
public class RecruitController {

	private final RecruitService recruitService;

	@GetMapping("/{boardCode}/posts")
	public Page<RecruitPostResponse> list(@PathVariable RecruitBoardCode boardCode,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		return recruitService.list(boardCode, PageRequest.of(page, size));
	}

	@GetMapping("/posts/{id}")
	public RecruitPostResponse get(@PathVariable Long id) {
		return recruitService.get(id);
	}

	@PostMapping("/{boardCode}/posts")
	public RecruitPostResponse create(
		@PathVariable RecruitBoardCode boardCode,
		@Valid @RequestBody RecruitPostUpsertRequest req
	) {
		Actor actor = ActorResolver.resolveOrGuest();
		return recruitService.create(boardCode, req, actor);
	}

	@PatchMapping("/posts/{id}")
	public RecruitPostResponse update(@PathVariable Long id,
		@Valid @RequestBody RecruitPostUpsertRequest req,
		@RequestParam(required = false) String password) {

		Actor actor = ActorResolver.resolveOrGuest();
		return recruitService.update(id, req, actor, password);
	}

	@DeleteMapping("/posts/{id}")
	public void delete(@PathVariable Long id,
		@RequestParam(required = false) String password) {

		Actor actor = ActorResolver.resolveOrGuest();
		recruitService.delete(id, actor, password);
	}

	@PatchMapping("/posts/{id}/pin")
	public ResponseEntity<Void> pin(
		@PathVariable Long id,
		@Valid @RequestBody RecruitPostPinRequest req
	) {
		Actor actor = ActorResolver.resolveOrGuest();
		recruitService.updatePinned(id, req.getPinned(), actor);
		return ResponseEntity.noContent().build();
	}
}
