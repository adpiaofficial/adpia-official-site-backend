package org.adpia.official.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateProfileRequest {
	private String name;
	private String department;
	private String gender;
	private Integer generation;
}
