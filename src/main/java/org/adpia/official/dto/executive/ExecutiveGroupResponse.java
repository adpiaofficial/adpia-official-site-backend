package org.adpia.official.dto.executive;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ExecutiveGroupResponse {
	private String title;
	private List<ExecutiveMemberResponse> members;
}