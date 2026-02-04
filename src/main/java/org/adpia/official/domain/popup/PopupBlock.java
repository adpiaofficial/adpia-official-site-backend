package org.adpia.official.domain.popup;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
	name = "popup_blocks",
	indexes = {
		@Index(name = "idx_popup_blocks_popup_order", columnList = "popup_id, sort_order")
	},
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_popup_blocks_popup_sort", columnNames = {"popup_id", "sort_order"})
	}
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PopupBlock {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "popup_id", nullable = false)
	private Long popupId;

	@Column(name = "sort_order", nullable = false)
	private int sortOrder;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private PopupBlockType type;

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
