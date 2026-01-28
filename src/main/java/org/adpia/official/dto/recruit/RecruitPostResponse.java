package org.adpia.official.dto.recruit;

import java.time.LocalDateTime;
import java.util.List;

import org.adpia.official.domain.recruit.RecruitBoardCode;
import org.adpia.official.domain.recruit.RecruitPost;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RecruitPostResponse {

	private Long id;
	private RecruitBoardCode boardCode;
	private String title;

	private Long authorMemberId;
	private String authorType;
	private String authorName;

	private boolean secret;
	private boolean locked;
	private boolean pinned;
	private boolean commentEnabled;
	private boolean likeEnabled;
	private int viewCount;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private List<RecruitBlockResponse> blocks;

	public static RecruitPostResponse from(RecruitPost post, List<RecruitBlockResponse> blocks) {
		return from(post, blocks, false);
	}

	public static RecruitPostResponse from(RecruitPost post, List<RecruitBlockResponse> blocks, boolean locked) {
		return RecruitPostResponse.builder()
			.id(post.getId())
			.boardCode(post.getBoardCode())
			.title(post.getTitle())
			.authorMemberId(post.getAuthorMemberId())
			.authorType(post.getAuthorType().name())
			.authorName(post.getAuthorName())
			.secret(post.isSecret())
			.locked(locked)
			.pinned(post.isPinned())
			.commentEnabled(post.isCommentEnabled())
			.likeEnabled(post.isLikeEnabled())
			.viewCount(post.getViewCount())
			.createdAt(post.getCreatedAt())
			.updatedAt(post.getUpdatedAt())
			.blocks(blocks)
			.build();
	}
}
