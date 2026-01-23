package org.adpia.official.domain.file.controller;

import org.adpia.official.domain.file.service.PresignedUrlService;
import org.adpia.official.dto.file.PresignRequest;
import org.adpia.official.dto.file.PresignResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

	private final PresignedUrlService presignedUrlService;

	@PostMapping("/presign")
	public PresignResponse presign(@Valid @RequestBody PresignRequest req) {
		var result = presignedUrlService.createPutUrl(
			req.boardCode(), req.postId(), req.contentType(), req.originalFilename()
		);
		return new PresignResponse(result.putUrl(), result.key(), result.fileUrl());
	}
}