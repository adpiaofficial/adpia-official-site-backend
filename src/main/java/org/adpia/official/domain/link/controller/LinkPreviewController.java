package org.adpia.official.domain.link.controller;

import lombok.RequiredArgsConstructor;
import org.adpia.official.domain.link.service.LinkPreviewService;
import org.adpia.official.dto.link.LinkPreviewResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/link")
public class LinkPreviewController {

	private final LinkPreviewService linkPreviewService;

	@GetMapping("/preview")
	public LinkPreviewResponse preview(@RequestParam String url) {
		return linkPreviewService.preview(url);
	}
}
