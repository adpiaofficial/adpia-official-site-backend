package org.adpia.official.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

	// docker-compose의 environment에서 설정한 값을 읽어옵니다.
	@Value("${spring.data.redis.host:localhost}")
	private String host;

	@Value("${spring.data.redis.port:6379}")
	private int port;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		// 기본 생성자가 아닌 RedisStandaloneConfiguration을 사용하여 호스트와 포트를 명시합니다.
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
		return new LettuceConnectionFactory(config);
	}

	@Bean
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory cf) {
		RedisTemplate<String, String> template = new RedisTemplate<>();
		template.setConnectionFactory(cf);

		StringRedisSerializer stringSerializer = new StringRedisSerializer();

		// 직렬화 설정 (기존 코드 유지)
		template.setKeySerializer(stringSerializer);
		template.setValueSerializer(stringSerializer);
		template.setHashKeySerializer(stringSerializer);
		template.setHashValueSerializer(stringSerializer);

		template.afterPropertiesSet();
		return template;
	}
}