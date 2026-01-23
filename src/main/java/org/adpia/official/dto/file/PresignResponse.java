package org.adpia.official.dto.file;

public record PresignResponse(
	String putUrl,
	String key,
	String fileUrl
) {}
