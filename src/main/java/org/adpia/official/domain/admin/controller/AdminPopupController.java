package org.adpia.official.domain.admin.controller;

import org.adpia.official.domain.popup.service.PopupService;
import org.adpia.official.dto.popup.PopupResponse;
import org.adpia.official.dto.popup.PopupUpsertRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/popups")
@RequiredArgsConstructor
public class AdminPopupController {

	private final PopupService popupService;

	@GetMapping("/current")
	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public ResponseEntity<PopupResponse> getCurrent() {
		return popupService.getCurrentForAdmin()
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.ok(null));
	}

	@PutMapping("/current")
	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public ResponseEntity<PopupResponse> saveCurrent(@RequestBody PopupUpsertRequest req) {
		return ResponseEntity.ok(popupService.saveCurrentForAdmin(req));
	}
}
