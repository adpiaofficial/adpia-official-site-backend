package org.adpia.official.dto.recruit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.adpia.official.domain.recruit.RecruitAuthorType;
import org.adpia.official.domain.recruit.RecruitComment;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RecruitCommentResponse {
	private Long id;
	private Long postId;
	private Long parentId;

	private String authorType;
	private Long authorMemberId;
	private String authorName;

	private String content;
	private boolean deleted;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@Builder.Default
	private List<RecruitCommentResponse> children = new ArrayList<>();

	public static RecruitCommentResponse from(RecruitComment c) {
		return RecruitCommentResponse.builder()
			.id(c.getId())
			.postId(c.getPostId())
			.parentId(c.getParentId())
			.authorType(c.getAuthorType().name())
			.authorMemberId(c.getAuthorMemberId())
			.authorName(c.getAuthorName())
			.content(c.isDeleted() ? "삭제된 댓글입니다." : c.getContent())
			.deleted(c.isDeleted())
			.createdAt(c.getCreatedAt())
			.updatedAt(c.getUpdatedAt())
			.build();
	}
}
