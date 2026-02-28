package org.adpia.official.domain.history.controller;

import java.util.List;

import org.adpia.official.domain.history.service.HistoryService;
import org.adpia.official.dto.history.HistoryResponse;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/history")
public class HistoryController {

	private final HistoryService historyService;

	@GetMapping
	public List<HistoryResponse> list(
		@RequestParam(required = false) Integer decade
	) {
		int resolvedDecade = (decade != null) ? decade : currentDecade();
		return historyService.list(resolvedDecade);
	}

	@GetMapping("/decades")
	public List<Integer> decades() {
		return historyService.decades();
	}

	private int currentDecade() {
		int year = java.time.Year.now().getValue();
		return (year / 10) * 10;
	}
}