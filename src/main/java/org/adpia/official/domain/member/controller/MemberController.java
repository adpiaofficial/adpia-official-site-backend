package org.adpia.official.domain.member.controller;

import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.adpia.official.domain.member.Member;
import org.adpia.official.domain.member.service.TokenBlacklistService;
import org.adpia.official.dto.member.AuthResponse;
import org.adpia.official.dto.member.LoginRequest;
import org.adpia.official.dto.member.MemberResponse;
import org.adpia.official.dto.member.SignupRequest;
import org.adpia.official.dto.member.UpdateGradeRequest;
import org.adpia.official.dto.member.UpdateProfileRequest;
import org.adpia.official.domain.member.service.MemberService;
import org.adpia.official.security.MemberPrincipal;
import org.adpia.official.security.jwt.JwtTokenProvider;
import org.adpia.official.security.jwt.RefreshTokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenService refreshTokenService;
	private final TokenBlacklistService tokenBlacklistService;



	@PostMapping("/signup")
	public ResponseEntity<Void> signUp(@RequestBody SignupRequest request) {
		memberService.signup(request);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(
		@RequestBody LoginRequest request,
		HttpServletResponse response
	) {
		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(
				request.getEmail(), request.getPassword()
			)
		);

		MemberPrincipal principal = (MemberPrincipal) authentication.getPrincipal();

		String accessToken = jwtTokenProvider.generateAccessToken(principal);
		String refreshToken = jwtTokenProvider.generateRefreshToken(principal.getUsername());

		refreshTokenService.save(
			principal.getUsername(),
			refreshToken,
			jwtTokenProvider.getRefreshExpirationMillis()
		);

		ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
			.httpOnly(true)
			.secure(false) // https 쓸 거면 true 로
			.path("/")
			.sameSite("Lax")
			.maxAge(jwtTokenProvider.getRefreshExpirationMillis() / 1000)
			.build();

		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

		return ResponseEntity.ok(new AuthResponse(accessToken));
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(
		HttpServletRequest request,
		@AuthenticationPrincipal MemberPrincipal principal
	) {
		// 1) Authorization 헤더에서 Access Token 추출
		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) {
			String token = header.substring(7);

			if (jwtTokenProvider.validateToken(token)) {
				Date exp = jwtTokenProvider.getExpiration(token);
				long millisToExpire = exp.getTime() - System.currentTimeMillis();

				// 3) 블랙리스트 등록
				tokenBlacklistService.blacklist(token, millisToExpire);
			}
		}
		if (principal != null) {
			refreshTokenService.delete(principal.getUsername());
		}

		return ResponseEntity.ok().build();
	}

	@GetMapping("/me")
	public ResponseEntity<MemberResponse> getCurrentUser() {
		MemberResponse response = memberService.getCurrentMember();
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/update")
	public ResponseEntity<MemberResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
		MemberResponse updated = memberService.updateProfile(request);
		return ResponseEntity.ok(updated);
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refresh(
		@CookieValue(value = "refreshToken", required = false) String refreshToken
	) {
		if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String email = jwtTokenProvider.getEmailFromToken(refreshToken);
		String stored = refreshTokenService.get(email);

		if (stored == null || !stored.equals(refreshToken)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Member member = memberService.getMemberByEmail(email);
		MemberPrincipal principal = new MemberPrincipal(member);

		String newAccessToken = jwtTokenProvider.generateAccessToken(principal);
		return ResponseEntity.ok(new AuthResponse(newAccessToken));
	}

}
