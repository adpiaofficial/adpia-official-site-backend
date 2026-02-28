package org.adpia.official.dto.law;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BylawUpsertRequest {
	@NotBlank
	private String content;
}