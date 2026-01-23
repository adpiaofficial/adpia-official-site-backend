package org.adpia.official.common;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
		// "존재하지 않는 게시글" 같이 리소스 없음은 404로
		if (e.getMessage() != null && e.getMessage().contains("존재하지")) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("message", e.getMessage()));
		}
		return ResponseEntity.badRequest()
			.body(Map.of("message", e.getMessage()));
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<?> handleIllegalState(IllegalStateException e) {
		// 권한/정책 위반은 403으로 처리하는 편이 많음
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
			.body(Map.of("message", e.getMessage()));
	}
}