package org.adpia.official.domain.recruit;

public enum RecruitBlockType {
	TEXT,   // 일반 텍스트(마크다운/HTML 포함 가능)
	IMAGE,  // 이미지
	VIDEO,  // 영상(mp4 등)
	FILE,   // 문서(pdf, ppt, hwp, docx 등)
	LINK,   // 하이퍼링크(단순 링크)
	EMBED   // 임베드(유튜브/인스타/네이버카페 등)
}