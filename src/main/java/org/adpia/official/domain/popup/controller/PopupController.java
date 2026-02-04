package org.adpia.official.domain.popup.controller;

import org.adpia.official.domain.popup.service.PopupService;
import org.adpia.official.dto.popup.PopupResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/popups")
@RequiredArgsConstructor
public class PopupController {

	private final PopupService popupService;

	@GetMapping("/active")
	public ResponseEntity<PopupResponse> getActivePopup() {
		return popupService.getActivePopup()
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.ok(null));
	}
}