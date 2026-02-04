package org.adpia.official.domain.popup.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.adpia.official.domain.popup.Popup;
import org.adpia.official.domain.popup.PopupBlock;
import org.adpia.official.domain.popup.PopupBlockType;
import org.adpia.official.domain.popup.PopupDetailLinkType;
import org.adpia.official.domain.popup.repository.PopupBlockRepository;
import org.adpia.official.domain.popup.repository.PopupRepository;
import org.adpia.official.dto.popup.PopupBlockRequest;
import org.adpia.official.dto.popup.PopupBlockResponse;
import org.adpia.official.dto.popup.PopupResponse;
import org.adpia.official.dto.popup.PopupUpsertRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopupService {

	private final PopupRepository popupRepository;
	private final PopupBlockRepository popupBlockRepository;

	public Optional<PopupResponse> getActivePopup() {
		LocalDateTime now = LocalDateTime.now();
		return popupRepository.findActive(now)
			.map(this::toResponseWithBlocks);
	}

	public Optional<PopupResponse> getCurrentForAdmin() {
		return popupRepository.findTopByOrderByIdDesc()
			.map(this::toResponseWithBlocks);
	}

	@Transactional
	public PopupResponse saveCurrentForAdmin(PopupUpsertRequest req) {
		validate(req);

		LocalDateTime now = LocalDateTime.now();

		// 1. 기존 팝업을 가져오거나 새로 생성
		Popup popup = popupRepository.findTopByOrderByIdDesc()
			.orElseGet(() -> Popup.builder()
				.title("메인 팝업")
				.active(false)
				.detailLinkType(PopupDetailLinkType.NONE)
				.createdAt(now)
				.updatedAt(now)
				.build());

		normalizeDetail(req);

		// 2. 팝업 정보 업데이트
		popup.setTitle(req.getTitle().trim());
		popup.setActive(req.isActive());
		popup.setStartAt(req.getStartAt());
		popup.setEndAt(req.getEndAt());
		popup.setDetailLabel(req.getDetailLinkType() == PopupDetailLinkType.NONE ? null : safe(req.getDetailLabel(), "자세히보기"));
		popup.setDetailLinkType(req.getDetailLinkType());
		popup.setDetailTargetId(req.getDetailTargetId());
		popup.setDetailUrl(req.getDetailUrl());
		popup.setUpdatedAt(now);

		Popup saved = popupRepository.save(popup);

		// 3. 기존 블록 삭제 및 즉시 반영 (중요!)
		// delete 쿼리가 insert 보다 늦게 나가는 것을 방지하기 위해 flush 호출
		popupBlockRepository.deleteByPopupId(saved.getId());
		popupBlockRepository.flush();

		// 4. 새로운 블록 저장
		List<PopupBlockRequest> blocks = req.getBlocks() == null ? Collections.emptyList() : req.getBlocks();
		for (int i = 0; i < blocks.size(); i++) {
			PopupBlockRequest b = blocks.get(i);

			PopupBlock block = PopupBlock.builder()
				.popupId(saved.getId())
				.sortOrder(i) // 프론트 전달 값보다 루프 인덱스(i)를 사용하는 것이 중복 방지에 더 안전합니다.
				.type(PopupBlockType.valueOf(b.getType()))
				.text(b.getText())
				.url(b.getUrl())
				.meta(b.getMeta())
				.createdAt(now)
				.build();
			popupBlockRepository.save(block);
		}

		return toResponseWithBlocks(saved);
	}

	private PopupResponse toResponseWithBlocks(Popup p) {
		List<PopupBlock> blocks = popupBlockRepository.findByPopupIdOrderBySortOrderAsc(p.getId());

		List<PopupBlockResponse> blockRes = blocks.stream()
			.map(b -> PopupBlockResponse.builder()
				.id(b.getId())
				.type(b.getType().name())
				.sortOrder(b.getSortOrder())
				.text(b.getText())
				.url(b.getUrl())
				.meta(b.getMeta())
				.build())
			.toList();

		return PopupResponse.builder()
			.id(p.getId())
			.title(p.getTitle())
			.active(p.isActive())
			.startAt(p.getStartAt())
			.endAt(p.getEndAt())
			.blocks(blockRes)
			.detailLabel(p.getDetailLabel())
			.detailLinkType(p.getDetailLinkType())
			.detailTargetId(p.getDetailTargetId())
			.detailUrl(p.getDetailUrl())
			.createdAt(p.getCreatedAt())
			.updatedAt(p.getUpdatedAt())
			.build();
	}

	private void validate(PopupUpsertRequest req) {
		if (req.getTitle() == null || req.getTitle().trim().isEmpty()) {
			throw new IllegalArgumentException("title은 필수입니다.");
		}
		if (req.getDetailLinkType() == null) {
			throw new IllegalArgumentException("detailLinkType은 필수입니다.");
		}
		if (req.getStartAt() != null && req.getEndAt() != null && req.getEndAt().isBefore(req.getStartAt())) {
			throw new IllegalArgumentException("endAt은 startAt보다 빠를 수 없습니다.");
		}
	}

	private void normalizeDetail(PopupUpsertRequest req) {
		PopupDetailLinkType t = req.getDetailLinkType();

		if (t == PopupDetailLinkType.NONE) {
			req.setDetailLabel(null);
			req.setDetailTargetId(null);
			req.setDetailUrl(null);
			return;
		}

		if (t == PopupDetailLinkType.NOTICE || t == PopupDetailLinkType.QA) {
			if (req.getDetailTargetId() == null) {
				throw new IllegalArgumentException(t + "는 detailTargetId가 필요합니다.");
			}
			req.setDetailUrl(null);
			return;
		}

		if (t == PopupDetailLinkType.PAGE) {
			String url = req.getDetailUrl() == null ? "" : req.getDetailUrl().trim();
			if (url.isEmpty()) throw new IllegalArgumentException("PAGE는 detailUrl(내부 경로)이 필요합니다.");
			if (!url.startsWith("/")) url = "/" + url;
			req.setDetailUrl(url);
			req.setDetailTargetId(null);
			return;
		}

		if (t == PopupDetailLinkType.EXTERNAL) {
			String url = req.getDetailUrl() == null ? "" : req.getDetailUrl().trim();
			if (!url.startsWith("https://")) throw new IllegalArgumentException("EXTERNAL은 https:// 링크만 허용합니다.");
			req.setDetailUrl(url);
			req.setDetailTargetId(null);
		}
	}

	private String safe(String v, String fallback) {
		if (v == null) return fallback;
		String t = v.trim();
		return t.isEmpty() ? fallback : t;
	}
}