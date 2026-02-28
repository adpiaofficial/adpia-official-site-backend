package org.adpia.official.domain.law.service;

import java.time.LocalDateTime;

import org.adpia.official.domain.law.ClubBylaw;
import org.adpia.official.domain.law.repository.ClubBylawRepository;
import org.adpia.official.dto.law.BylawResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClubBylawService {

	private final ClubBylawRepository repo;
	private static final Long FIXED_ID = 1L;

	@Transactional(readOnly = true)
	public BylawResponse getOrNull() {
		return repo.findById(FIXED_ID).map(BylawResponse::from).orElse(null);
	}

	@Transactional
	public BylawResponse upsert(String content, String updatedBy) {
		ClubBylaw e = repo.findById(FIXED_ID)
			.orElse(ClubBylaw.builder()
				.id(FIXED_ID)
				.content("")
				.updatedAt(LocalDateTime.now())
				.updatedBy(updatedBy != null ? updatedBy : "SYSTEM")
				.build());

		e.upsert(content, updatedBy != null ? updatedBy : "SYSTEM");
		repo.save(e);

		return BylawResponse.from(e);
	}
}