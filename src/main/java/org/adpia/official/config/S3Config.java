package org.adpia.official.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

	@Bean
	public Region awsRegion(@Value("${AWS_REGION}") String region) {
		return Region.of(region);
	}

	@Bean
	public StaticCredentialsProvider awsCreds(
		@Value("${AWS_ACCESS_KEY_ID}") String accessKey,
		@Value("${AWS_SECRET_ACCESS_KEY}") String secretKey
	) {
		return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
	}

	@Bean
	public S3Client s3Client(Region region, StaticCredentialsProvider creds) {
		return S3Client.builder().region(region).credentialsProvider(creds).build();
	}

	@Bean
	public S3Presigner s3Presigner(Region region, StaticCredentialsProvider creds) {
		return S3Presigner.builder().region(region).credentialsProvider(creds).build();
	}
}
