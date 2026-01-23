package org.adpia.official.domain.recruit;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
	name = "recruit_post_blocks",
	indexes = {
		@Index(name = "idx_blocks_post_order", columnList = "post_id, sort_order")
	},
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_blocks_post_sort", columnNames = {"post_id", "sort_order"})
	}
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RecruitPostBlock {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "post_id", nullable = false)
	private Long postId;

	@Column(name = "sort_order", nullable = false)
	private int sortOrder;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private RecruitBlockType type;

	@Column(columnDefinition = "text")
	private String text;

	@Column(length = 2048)
	private String url;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "meta", columnDefinition = "jsonb")
	private String meta;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
}
