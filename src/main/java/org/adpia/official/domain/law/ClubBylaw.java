package org.adpia.official.domain.law;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "club_bylaw")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubBylaw {

	@Id
	private Long id;

	@Lob
	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@PrePersist
	void prePersist() {
		if (updatedAt == null) updatedAt = LocalDateTime.now();
		if (updatedBy == null) updatedBy = "SYSTEM";
		if (content == null) content = "";
	}

	public void upsert(String content, String updatedBy) {
		this.content = content;
		this.updatedBy = updatedBy;
		this.updatedAt = LocalDateTime.now();
	}
}