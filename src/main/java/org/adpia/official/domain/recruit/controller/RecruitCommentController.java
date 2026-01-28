package org.adpia.official.domain.recruit.controller;

import java.util.List;

import org.adpia.official.domain.recruit.service.RecruitCommentService;
import org.adpia.official.domain.recruit.service.RecruitService.Actor;
import org.adpia.official.dto.recruit.RecruitCommentCreateRequest;
import org.adpia.official.dto.recruit.RecruitCommentResponse;
import org.adpia.official.security.ActorResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruit")
public class RecruitCommentController {

	private final RecruitCommentService commentService;
	private final ActorResolver actorResolver;

	@GetMapping("/posts/{postId}/comments")
	public List<RecruitCommentResponse> list(@PathVariable Long postId) {
		return commentService.list(postId);
	}

	@PostMapping("/posts/{postId}/comments")
	public RecruitCommentResponse create(
		@PathVariable Long postId,
		@Valid @RequestBody RecruitCommentCreateRequest req
	) {
		Actor actor = actorResolver.resolveOrGuest();
		return commentService.create(postId, req, actor);
	}

	@DeleteMapping("/comments/{commentId}")
	public ResponseEntity<Void> delete(
		@PathVariable Long commentId,
		@RequestParam(required = false) String password
	) {
		Actor actor = actorResolver.resolveOrGuest();
		commentService.delete(commentId, actor, password);
		return ResponseEntity.noContent().build();
	}
}
