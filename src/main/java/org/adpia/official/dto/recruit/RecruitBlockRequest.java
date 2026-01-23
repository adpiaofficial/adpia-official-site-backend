package org.adpia.official.dto.recruit;

import org.adpia.official.domain.recruit.RecruitBlockType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RecruitBlockRequest {

	@NotNull
	private RecruitBlockType type;

	@Min(0)
	private int sortOrder;

	private String text; // TEXT용
	private String url;  // FILE/IMAGE/VIDEO/EMBED/LINK용
	private String meta; // json string
}