package org.adpia.official.dto.popup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PopupBlockRequest {
	private String type;
	private int sortOrder;
	private String text;
	private String url;
	private String meta;
}