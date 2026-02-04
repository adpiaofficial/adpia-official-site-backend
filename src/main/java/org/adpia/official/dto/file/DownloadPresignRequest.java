package org.adpia.official.dto.file;

import jakarta.validation.constraints.NotBlank;

public record DownloadPresignRequest(
	@NotBlank String key,
	String contentType,
	String originalFilename
) {}
