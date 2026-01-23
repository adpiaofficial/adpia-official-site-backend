package org.adpia.official.domain.recruit;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
	name = "recruit_posts",
	indexes = {
		@Index(name = "idx_recruit_posts_board_created", columnList = "board_code, created_at"),
		@Index(name = "idx_recruit_posts_board_pinned", columnList = "board_code, pinned, pinned_at")
	}
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RecruitPost {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "board_code", nullable = false, length = 30)
	private RecruitBoardCode boardCode;

	@Column(nullable = false, length = 200)
	private String title;

	@Column(name = "author_member_id")
	private Long authorMemberId;

	@Enumerated(EnumType.STRING)
	@Column(name = "author_type", nullable = false, length = 10)
	@Builder.Default
	private RecruitAuthorType authorType = RecruitAuthorType.MEMBER;

	@Column(name = "author_name", length = 50)
	private String authorName;

	@Column(name = "is_secret", nullable = false)
	@Builder.Default
	private boolean secret = false;

	@Column(name = "secret_password_hash", length = 200)
	private String secretPasswordHash;

	@Column(name = "pinned", nullable = false)
	@Builder.Default
	private boolean pinned = false;

	@Column(name = "pinned_at")
	private LocalDateTime pinnedAt;

	@Column(name = "comment_enabled", nullable = false)
	@Builder.Default
	private boolean commentEnabled = true;

	@Column(name = "like_enabled", nullable = false)
	@Builder.Default
	private boolean likeEnabled = true;

	@Column(name = "view_count", nullable = false)
	@Builder.Default
	private int viewCount = 0;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@PrePersist
	void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		this.createdAt = now;
		this.updatedAt = now;
	}

	@PreUpdate
	void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}
