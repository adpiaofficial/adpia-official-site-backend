package org.adpia.official.domain.history.service;

import java.util.List;

import org.adpia.official.domain.history.History;
import org.adpia.official.domain.history.repository.HistoryRepository;
import org.adpia.official.dto.history.HistoryCreateRequest;
import org.adpia.official.dto.history.HistoryResponse;
import org.adpia.official.dto.history.HistoryUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryService {

	private final HistoryRepository historyRepository;

	@Transactional(readOnly = true)
	public List<HistoryResponse> list(int decade) {
		int start = normalizeDecadeStart(decade);
		int end = start + 9;

		return historyRepository
			.findByYearBetweenOrderByYearDescMonthAscSortOrderAsc(start, end)
			.stream()
			.map(HistoryResponse::from)
			.toList();
	}

	@Transactional
	public HistoryResponse create(HistoryCreateRequest req) {
		validate(req.year(), req.month(), req.content());

		History h = History.builder()
			.year(req.year())
			.month(req.month())
			.content(req.content().trim())
			.sortOrder(req.sortOrder())
			.build();

		historyRepository.save(h);
		return HistoryResponse.from(h);
	}

	@Transactional
	public HistoryResponse update(Long id, HistoryUpdateRequest req) {
		validate(req.year(), req.month(), req.content());

		History h = historyRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 연혁입니다."));

		h.update(req.year(), req.month(), req.content().trim(), req.sortOrder());
		return HistoryResponse.from(h);
	}

	@Transactional
	public List<Integer> decades() {
		return historyRepository.findDistinctDecades();
	}

	private static int normalizeDecadeStart(int decade) {
		if (decade % 10 != 0) throw new IllegalArgumentException("decade는 10의 배수여야 합니다. 예) 2020");
		if (decade < 1990 || decade > 2100) throw new IllegalArgumentException("decade 범위가 올바르지 않습니다.");
		return decade;
	}

	private static void validate(int year, int month, String content) {
		if (year < 1992 || year > 2100) throw new IllegalArgumentException("연도가 올바르지 않습니다.");
		if (month < 1 || month > 12) throw new IllegalArgumentException("월은 1~12만 가능합니다.");
		if (content == null || content.trim().isEmpty()) throw new IllegalArgumentException("내용을 입력해주세요.");
	}
}