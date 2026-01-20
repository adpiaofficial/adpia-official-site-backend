package org.adpia.official.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.adpia.official.security.MemberPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.access-expiration-millis}")
	@Getter
	private long accessExpirationMillis;

	@Value("${jwt.refresh-expiration-millis}")
	@Getter
	private long refreshExpirationMillis;

	private SecretKey key;

	@PostConstruct
	public void init() {
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	// Access Token
	public String generateAccessToken(MemberPrincipal principal) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + accessExpirationMillis);

		return Jwts.builder()
			.subject(principal.getUsername())
			.claim("role", principal.getAuthorities().iterator().next().getAuthority())
			.issuedAt(now)
			.expiration(expiry)
			.signWith(key, Jwts.SIG.HS256)
			.compact();
	}

	public String generateRefreshToken(String email) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + refreshExpirationMillis);

		return Jwts.builder()
			.subject(email)
			.issuedAt(now)
			.expiration(expiry)
			.signWith(key, Jwts.SIG.HS256)
			.compact();
	}

	public String getEmailFromToken(String token) {
		return parseToken(token).getPayload().getSubject();
	}

	public Date getExpiration(String token) {
		return parseToken(token).getPayload().getExpiration();
	}

	public boolean validateToken(String token) {
		try {
			parseToken(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	private Jws<Claims> parseToken(String token) {
		return Jwts.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(token);
	}
}
