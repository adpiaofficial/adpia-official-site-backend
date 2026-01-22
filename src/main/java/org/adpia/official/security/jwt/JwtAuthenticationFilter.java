package org.adpia.official.security.jwt;

import java.io.IOException;

import org.adpia.official.domain.member.service.TokenBlacklistService;
import org.adpia.official.security.MemberDetailsService;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final MemberDetailsService memberDetailsService;
	private final TokenBlacklistService tokenBlacklistService;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getServletPath();
		return path.startsWith("/api/email/") ||
			path.startsWith("/api/members/signup") ||
			path.startsWith("/api/members/login") ||
			path.equals("/health");
	}

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {

		String token = resolveToken(request);

		if (token != null
			&& jwtTokenProvider.validateToken(token)
			&& !tokenBlacklistService.isBlacklisted(token)) {

			try {
				String email = jwtTokenProvider.getEmailFromToken(token);
				UserDetails userDetails = memberDetailsService.loadUserByUsername(email);

				if (!userDetails.isEnabled()) {
					writeUnauthorized(response, "ACCOUNT_DISABLED", "비활성화된 계정입니다.");
					return;
				}

				UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());

				SecurityContextHolder.getContext().setAuthentication(authentication);

			} catch (DisabledException e) {
				SecurityContextHolder.clearContext();
				writeUnauthorized(response, "ACCOUNT_DISABLED", "비활성화된 계정입니다.");
				return;
			} catch (Exception e) {
				SecurityContextHolder.clearContext();
			}
		}

		filterChain.doFilter(request, response);
	}

	private void writeUnauthorized(HttpServletResponse response, String code, String message) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(
			("{\"code\":\"" + code + "\",\"message\":\"" + message + "\"}")
		);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearer = request.getHeader("Authorization");
		if (bearer != null && bearer.startsWith("Bearer ")) {
			return bearer.substring(7);
		}
		return null;
	}
}
