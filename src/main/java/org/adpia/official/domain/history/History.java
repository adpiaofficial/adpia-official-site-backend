package org.adpia.official.domain.history;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "history")
public class History {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable=false)
	private int year;

	@Column(nullable=false)
	private int month;

	@Column(nullable=false, columnDefinition="TEXT")
	private String content;

	@Column(name="sort_order", nullable=false)
	@Builder.Default
	private int sortOrder = 0;

	@Column(name="created_at", nullable=false, updatable=false)
	private LocalDateTime createdAt;

	@Column(name="updated_at", nullable=false)
	private LocalDateTime updatedAt;

	@PrePersist
	void prePersist() {
		if (createdAt == null) createdAt = LocalDateTime.now();
		if (updatedAt == null) updatedAt = createdAt;
	}

	@PreUpdate
	void preUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public void update(int year, int month, String content, int sortOrder) {
		this.year = year;
		this.month = month;
		this.content = content;
		this.sortOrder = sortOrder;
	}
}