package org.adpia.official.dto.history;

import java.time.LocalDateTime;

import org.adpia.official.domain.history.History;

public record HistoryResponse(
	Long id,
	int year,
	int month,
	String content,
	int sortOrder,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public static HistoryResponse from(History h) {
		return new HistoryResponse(
			h.getId(),
			h.getYear(),
			h.getMonth(),
			h.getContent(),
			h.getSortOrder(),
			h.getCreatedAt(),
			h.getUpdatedAt()
		);
	}
}