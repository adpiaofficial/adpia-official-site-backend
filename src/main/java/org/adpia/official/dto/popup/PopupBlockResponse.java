package org.adpia.official.dto.popup;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PopupBlockResponse {
	private Long id;
	private String type;
	private int sortOrder;
	private String text;
	private String url;
	private String meta;
}