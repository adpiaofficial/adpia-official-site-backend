package org.adpia.official.domain.recruit;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "recruit_comments")
public class RecruitComment {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name="post_id", nullable=false)
	private Long postId;

	@Column(name="parent_id")
	private Long parentId;

	@Enumerated(EnumType.STRING)
	@Column(name="author_type", nullable=false)
	private RecruitAuthorType authorType;

	@Column(name="author_member_id")
	private Long authorMemberId;

	@Column(name="author_name", nullable=false)
	private String authorName;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(name="password_hash")
	private String passwordHash;

	@Column(nullable=false)
	private boolean deleted;

	@Column(name="created_at", nullable=false)
	private LocalDateTime createdAt;

	@Column(name="updated_at", nullable=false)
	private LocalDateTime updatedAt;
}
