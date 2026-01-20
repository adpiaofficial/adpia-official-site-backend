package org.adpia.official.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {
	private String name;
	private String department;
	private String email;
	private String password;
	private String gender;
	private int generation;
}
