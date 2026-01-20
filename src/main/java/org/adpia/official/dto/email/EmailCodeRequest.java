package org.adpia.official.dto.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailCodeRequest(
	@NotBlank
	@Email
	String email
) {
}