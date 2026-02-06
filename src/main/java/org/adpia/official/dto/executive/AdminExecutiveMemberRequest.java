package org.adpia.official.dto.executive;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AdminExecutiveMemberRequest {

	private Long id;

	@NotBlank
	private String role;

	@NotBlank
	private String generation;

	@NotBlank
	private String department;

	@NotBlank
	private String name;

	private String imageUrl;

	@NotNull
	private Integer orderIndex;

	private Boolean active;
}
