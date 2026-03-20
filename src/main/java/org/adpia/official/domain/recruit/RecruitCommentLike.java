package org.adpia.official.domain.recruit;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
	name = "recruit_comment_likes",
	uniqueConstraints = {
		@UniqueConstraint(name = "uq_recruit_comment_like", columnNames = {"comment_id", "member_id"})
	}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitCommentLike {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "comment_id", nullable = false)
	private Long commentId;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
}