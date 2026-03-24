package org.adpia.official.domain.recruit;

public enum RecruitBoardCode {
	NOTICE,
	QA,
	NEWS,
	HUNDRED_QNA,
	THREE_MIN_SPEECH;

	public boolean isPinnable() {
		return true;
	}

	public boolean isCommentEnabledByDefault() {
		return switch (this) {
			case NOTICE -> false;
			case QA, NEWS, HUNDRED_QNA, THREE_MIN_SPEECH -> true;
		};
	}

	public boolean canGuestCreate() {
		return this == QA;
	}

	public boolean isAdminWriteOnly() {
		return this == NOTICE || this == NEWS;
	}
}