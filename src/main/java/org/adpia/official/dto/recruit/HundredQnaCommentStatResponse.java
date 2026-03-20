package org.adpia.official.dto.recruit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HundredQnaCommentStatResponse {

	private Long memberId;
	private String memberName;
	private String department;
	private int generation;
	private long commentCount;
}