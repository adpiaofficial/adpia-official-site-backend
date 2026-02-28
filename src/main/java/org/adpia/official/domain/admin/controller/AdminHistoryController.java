package org.adpia.official.domain.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.Year;
import java.util.List;

import org.adpia.official.domain.history.service.HistoryService;
import org.adpia.official.dto.history.HistoryCreateRequest;
import org.adpia.official.dto.history.HistoryResponse;
import org.adpia.official.dto.history.HistoryUpdateRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/history")
public class AdminHistoryController {

	private final HistoryService historyService;

	@GetMapping
	public List<HistoryResponse> list(@RequestParam(required = false) Integer decade) {
		int resolved = (decade != null) ? decade : currentDecade();
		return historyService.list(resolved);
	}

	@PostMapping
	public HistoryResponse create(@Valid @RequestBody HistoryCreateRequest req) {
		return historyService.create(req);
	}

	@PatchMapping("/{id}")
	public HistoryResponse update(
		@PathVariable Long id,
		@Valid @RequestBody HistoryUpdateRequest req
	) {
		return historyService.update(id, req);
	}


	private int currentDecade() {
		int year = Year.now().getValue();
		return (year / 10) * 10;
	}
}