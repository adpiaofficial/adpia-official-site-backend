package org.adpia.official.domain.recruit;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
	name = "recruit_post_likes",
	uniqueConstraints = {
		@UniqueConstraint(name = "uq_recruit_post_like", columnNames = {"post_id", "member_id"})
	}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitPostLike {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "post_id", nullable = false)
	private Long postId;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
}