package org.adpia.official.domain.file.service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class PresignedUrlService {

	private final S3Presigner presigner;

	@Value("${AWS_S3_BUCKET}")
	private String bucket;

	@Value("${AWS_S3_PUBLIC_BASE_URL:}")
	private String publicBaseUrl;

	public PresignResult createPutUrl(String boardCode, Long postId, String contentType, String originalFilename) {
		validateBoardCode(boardCode);
		validateContentType(contentType);

		String ext = guessExt(originalFilename, contentType);
		String key = buildKey(boardCode, postId, contentType, ext);

		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket(bucket)
			.key(key)
			.contentType(contentType)
			.build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.signatureDuration(Duration.ofMinutes(10))
			.putObjectRequest(putObjectRequest)
			.build();

		PresignedPutObjectRequest presigned = presigner.presignPutObject(presignRequest);

		String fileUrl = buildPublicUrl(key);

		return new PresignResult(presigned.url().toString(), key, fileUrl);
	}

	private String buildKey(String boardCode, Long postId, String contentType, String ext) {
		String typeFolder =
			contentType != null && contentType.startsWith("video") ? "videos"
				: contentType != null && contentType.startsWith("image") ? "images"
				: "files";

		String date = LocalDate.now().toString().replace("-", ""); // yyyyMMdd
		String postPart = (postId == null) ? "temp" : String.valueOf(postId);

		return "recruit/" + boardCode + "/" + date + "/" + postPart + "/" + typeFolder + "/" + UUID.randomUUID() + ext;
	}

	private String buildPublicUrl(String key) {
		if (publicBaseUrl != null && !publicBaseUrl.isBlank()) {
			String base = publicBaseUrl.endsWith("/") ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1) : publicBaseUrl;
			return base + "/" + key;
		}
		return "https://" + bucket + ".s3.amazonaws.com/" + key;
	}

	private void validateBoardCode(String boardCode) {
		if (boardCode == null || boardCode.isBlank()) {
			throw new IllegalArgumentException("boardCode가 필요합니다.");
		}
	}

	private void validateContentType(String contentType) {
		if (contentType == null || contentType.isBlank()) {
			throw new IllegalArgumentException("contentType이 필요합니다.");
		}

		if (contentType.startsWith("image/")) return;
		if (contentType.startsWith("video/")) return;
		if (contentType.equals("application/pdf")) return;
		if (contentType.equals("application/zip")) return;
		if (contentType.equals("text/plain")) return;

		throw new IllegalArgumentException("허용되지 않은 contentType: " + contentType);
	}

	private String guessExt(String filename, String contentType) {
		if (filename != null) {
			String trimmed = filename.trim();
			int dot = trimmed.lastIndexOf('.');
			if (dot >= 0 && dot < trimmed.length() - 1) {
				String ext = trimmed.substring(dot);
				if (ext.length() <= 10) return ext;
			}
		}

		if (contentType == null) return "";

		// image
		if ("image/png".equalsIgnoreCase(contentType)) return ".png";
		if ("image/jpeg".equalsIgnoreCase(contentType)) return ".jpg";
		if ("image/jpg".equalsIgnoreCase(contentType)) return ".jpg";
		if ("image/gif".equalsIgnoreCase(contentType)) return ".gif";
		if ("image/webp".equalsIgnoreCase(contentType)) return ".webp";
		if ("image/svg+xml".equalsIgnoreCase(contentType)) return ".svg";

		// video
		if ("video/mp4".equalsIgnoreCase(contentType)) return ".mp4";
		if ("video/webm".equalsIgnoreCase(contentType)) return ".webm";
		if ("video/quicktime".equalsIgnoreCase(contentType)) return ".mov";

		// docs
		if ("application/pdf".equalsIgnoreCase(contentType)) return ".pdf";
		if ("application/zip".equalsIgnoreCase(contentType)) return ".zip";
		if ("text/plain".equalsIgnoreCase(contentType)) return ".txt";

		return "";
	}

	public record PresignResult(String putUrl, String key, String fileUrl) {}
}
