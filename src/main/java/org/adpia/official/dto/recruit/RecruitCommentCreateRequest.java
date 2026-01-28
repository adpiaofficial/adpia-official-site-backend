package org.adpia.official.dto.recruit;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitCommentCreateRequest {
	private Long parentId;
	@NotBlank
	private String content;

	private String authorName;
	private String password;
}