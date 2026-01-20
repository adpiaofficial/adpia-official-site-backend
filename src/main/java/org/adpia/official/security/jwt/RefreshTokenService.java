package org.adpia.official.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

	private final RedisTemplate<String, String> redisTemplate;
	private static final String PREFIX = "RT:";

	public void save(String email, String refreshToken, long durationMs) {
		long seconds = durationMs / 1000;
		redisTemplate.opsForValue()
			.set(PREFIX + email, refreshToken, seconds, TimeUnit.SECONDS);
	}

	public String get(String email) {
		return redisTemplate.opsForValue().get(PREFIX + email);
	}

	public void delete(String email) {
		redisTemplate.delete(PREFIX + email);
	}
}
