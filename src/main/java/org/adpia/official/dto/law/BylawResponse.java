package org.adpia.official.dto.law;

import java.time.LocalDateTime;

import org.adpia.official.domain.law.ClubBylaw;

import lombok.*;

@Getter
@AllArgsConstructor
public class BylawResponse {
	private Long id;
	private String content;
	private LocalDateTime updatedAt;
	private String updatedBy;

	public static BylawResponse from(ClubBylaw e) {
		return new BylawResponse(e.getId(), e.getContent(), e.getUpdatedAt(), e.getUpdatedBy());
	}
}