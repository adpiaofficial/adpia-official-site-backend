package org.adpia.official.email.controller;

import lombok.RequiredArgsConstructor;

import org.adpia.official.dto.email.EmailCodeRequest;
import org.adpia.official.dto.email.EmailVerifyRequest;
import org.adpia.official.email.service.EmailVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailVerificationController {

	private final EmailVerificationService emailVerificationService;

	@PostMapping("/code")
	public ResponseEntity<Void> sendCode(@RequestBody @Validated EmailCodeRequest request) {
		emailVerificationService.sendVerificationCode(request.email());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/verify")
	public ResponseEntity<VerificationResult> verify(@RequestBody @Validated EmailVerifyRequest request) {
		boolean success = emailVerificationService.verifyCode(request.email(), request.code());
		if (success) {
			return ResponseEntity.ok(new VerificationResult(true, "인증에 성공했습니다."));
		}
		return ResponseEntity.badRequest().body(new VerificationResult(false, "인증에 실패했습니다."));
	}

	public record VerificationResult(boolean success, String message) {}
}