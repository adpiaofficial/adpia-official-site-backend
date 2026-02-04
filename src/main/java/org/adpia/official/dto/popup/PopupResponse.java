package org.adpia.official.dto.popup;

import lombok.Builder;
import lombok.Getter;
import org.adpia.official.domain.popup.PopupDetailLinkType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PopupResponse {
	private Long id;
	private String title;
	private boolean active;
	private LocalDateTime startAt;
	private LocalDateTime endAt;

	private List<PopupBlockResponse> blocks;

	private String detailLabel;
	private PopupDetailLinkType detailLinkType;
	private Long detailTargetId;
	private String detailUrl;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}