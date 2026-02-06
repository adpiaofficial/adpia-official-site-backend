package org.adpia.official.dto.executive;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExecutiveMemberResponse {
	private Long id;
	private String role;
	private String generation;
	private String department;
	private String name;
	private String imageUrl;
	private Integer orderIndex;
}