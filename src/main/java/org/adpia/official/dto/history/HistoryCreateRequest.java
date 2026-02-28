package org.adpia.official.dto.history;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record HistoryCreateRequest(
	@Min(1992) @Max(2100) int year,
	@Min(1) @Max(12) int month,
	@NotBlank String content,
	@Min(0) int sortOrder
) {}