package org.adpia.official.dto.popup;

import lombok.Getter;
import lombok.Setter;
import org.adpia.official.domain.popup.PopupDetailLinkType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PopupUpsertRequest {
	private String title;
	private boolean active;
	private LocalDateTime startAt;
	private LocalDateTime endAt;

	private List<PopupBlockRequest> blocks;

	private String detailLabel;
	private PopupDetailLinkType detailLinkType;
	private Long detailTargetId;
	private String detailUrl;
}