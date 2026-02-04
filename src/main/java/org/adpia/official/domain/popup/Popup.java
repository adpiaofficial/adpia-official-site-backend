package org.adpia.official.domain.popup;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "popups")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Popup {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private boolean active;

	private LocalDateTime startAt;
	private LocalDateTime endAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private PopupDetailLinkType detailLinkType;

	private String detailLabel;

	private Long detailTargetId;

	@Column(length = 2048)
	private String detailUrl;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		this.createdAt = now;
		this.updatedAt = now;
		if (this.detailLinkType == null) this.detailLinkType = PopupDetailLinkType.NONE;
	}

	@PreUpdate
	void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}
