package org.adpia.official.dto.recruit;

import org.adpia.official.domain.recruit.RecruitPostBlock;
import org.adpia.official.domain.recruit.RecruitBlockType;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RecruitBlockResponse {

	private RecruitBlockType type;
	private int sortOrder;
	private String text;
	private String url;
	private String meta;

	public static RecruitBlockResponse from(RecruitPostBlock b) {
		return RecruitBlockResponse.builder()
			.type(b.getType())
			.sortOrder(b.getSortOrder())
			.text(b.getText())
			.url(b.getUrl())
			.meta(b.getMeta())
			.build();
	}
}