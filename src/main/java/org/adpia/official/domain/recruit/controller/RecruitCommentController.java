package org.adpia.official.domain.recruit.controller;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.adpia.official.domain.recruit.service.RecruitCommentService;
import org.adpia.official.domain.recruit.service.RecruitLikeService;
import org.adpia.official.domain.recruit.service.RecruitService;
import org.adpia.official.dto.recruit.RecruitCommentCreateRequest;
import org.adpia.official.dto.recruit.RecruitCommentResponse;
import org.adpia.official.security.ActorResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruit")
public class RecruitCommentController {

	private final RecruitCommentService recruitCommentService;
	private final RecruitLikeService recruitLikeService;
	private final ActorResolver actorResolver;

	@GetMapping("/posts/{postId}/comments")
	public List<RecruitCommentResponse> list(@PathVariable Long postId) {
		RecruitService.Actor actor = actorResolver.resolveOrGuest();
		return recruitCommentService.list(postId, actor);
	}

	@PostMapping("/posts/{postId}/comments")
	public RecruitCommentResponse create(
		@PathVariable Long postId,
		@Valid @RequestBody RecruitCommentCreateRequest req
	) {
		RecruitService.Actor actor = actorResolver.resolveOrGuest();
		return recruitCommentService.create(postId, req, actor);
	}

	@DeleteMapping("/comments/{commentId}")
	public ResponseEntity<Void> delete(
		@PathVariable Long commentId,
		@RequestParam(required = false) String password
	) {
		RecruitService.Actor actor = actorResolver.resolveOrGuest();
		recruitCommentService.delete(commentId, actor, password);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/comments/{id}/like")
	public ResponseEntity<Void> likeComment(@PathVariable Long id) {
		RecruitService.Actor actor = actorResolver.resolveOrGuest();
		recruitLikeService.likeComment(id, actor);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/comments/{id}/like")
	public ResponseEntity<Void> unlikeComment(@PathVariable Long id) {
		RecruitService.Actor actor = actorResolver.resolveOrGuest();
		recruitLikeService.unlikeComment(id, actor);
		return ResponseEntity.noContent().build();
	}
}