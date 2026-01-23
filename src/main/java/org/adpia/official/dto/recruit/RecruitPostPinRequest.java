package org.adpia.official.dto.recruit;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecruitPostPinRequest {
	@NotNull
	private Boolean pinned;
}