package org.adpia.official.domain.member.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

	private final RedisTemplate<String, String> redisTemplate;
	private static final String PREFIX = "BLACKLIST:TOKEN:";

	public void blacklist(String token, long millisToExpire) {
		if (millisToExpire <= 0) return;
		long seconds = millisToExpire / 1000;
		redisTemplate
			.opsForValue()
			.set(PREFIX + token, "true", seconds, TimeUnit.SECONDS);
	}

	public boolean isBlacklisted(String token) {
		Boolean exists = redisTemplate.hasKey(PREFIX + token);
		return Boolean.TRUE.equals(exists);
	}
}
