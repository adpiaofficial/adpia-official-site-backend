package org.adpia.official.dto.recruit;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RecruitPostUpsertRequest {

	@NotBlank
	@Size(max = 200)
	private String title;

	// 외부(QA) 작성용
	private String authorName;

	private Boolean pinned;

	// 비밀글 옵션 (QA)
	private Boolean secret;
	private String password;

	@Valid
	private List<RecruitBlockRequest> blocks;
}