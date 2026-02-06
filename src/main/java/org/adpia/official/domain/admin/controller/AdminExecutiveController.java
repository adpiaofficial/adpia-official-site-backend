package org.adpia.official.domain.admin.controller;

import java.util.List;

import org.adpia.official.domain.executive.service.ExecutiveService;
import org.adpia.official.dto.executive.AdminExecutiveUpsertRequest;
import org.adpia.official.dto.executive.ExecutiveGroupResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/executives")
public class AdminExecutiveController {

	private final ExecutiveService executiveService;

	@GetMapping
	public List<ExecutiveGroupResponse> getAll() {
		return executiveService.getAllForAdmin();
	}

	@PutMapping
	public ResponseEntity<Void> upsertGroup(@Valid @RequestBody AdminExecutiveUpsertRequest req) {
		executiveService.upsertGroup(req);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		executiveService.deleteOne(id);
		return ResponseEntity.noContent().build();
	}
}