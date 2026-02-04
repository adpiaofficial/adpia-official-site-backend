package org.adpia.official.dto.link;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LinkPreviewResponse {
	private String url;
	private String siteName;
	private String title;
	private String desc;
	private String image;
}