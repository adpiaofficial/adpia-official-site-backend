package org.adpia.official.dto.executive;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class AdminExecutiveUpsertRequest {

	@NotBlank
	private String groupTitle;

	@NotEmpty
	private List<AdminExecutiveMemberRequest> members;
}