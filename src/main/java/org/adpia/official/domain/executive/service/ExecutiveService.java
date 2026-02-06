package org.adpia.official.domain.executive.service;

import lombok.RequiredArgsConstructor;
import org.adpia.official.domain.executive.ExecutiveMember;
import org.adpia.official.domain.executive.repository.ExecutiveMemberRepository;
import org.adpia.official.dto.executive.AdminExecutiveMemberRequest;
import org.adpia.official.dto.executive.AdminExecutiveUpsertRequest;
import org.adpia.official.dto.executive.ExecutiveGroupResponse;
import org.adpia.official.dto.executive.ExecutiveMemberResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExecutiveService {

	private final ExecutiveMemberRepository executiveMemberRepository;

	@Transactional
	public List<ExecutiveGroupResponse> getPublicExecutives() {
		return groupize(executiveMemberRepository.findAllByActiveTrueOrderByGroupTitleAscOrderIndexAscIdAsc());
	}

	@Transactional
	public List<ExecutiveGroupResponse> getAllForAdmin() {
		return groupize(executiveMemberRepository.findAllByOrderByGroupTitleAscOrderIndexAscIdAsc());
	}

	@Transactional
	public void upsertGroup(AdminExecutiveUpsertRequest req) {
		String groupTitle = req.getGroupTitle().trim();

		List<ExecutiveMember> existing = executiveMemberRepository.findAllByOrderByGroupTitleAscOrderIndexAscIdAsc()
			.stream()
			.filter(e -> e.getGroupTitle().equals(groupTitle))
			.collect(Collectors.toList());

		Map<Long, ExecutiveMember> existingMap = existing.stream()
			.filter(e -> e.getId() != null)
			.collect(Collectors.toMap(ExecutiveMember::getId, e -> e));

		Set<Long> touchedIds = new HashSet<>();

		for (AdminExecutiveMemberRequest m : req.getMembers()) {
			ExecutiveMember target;

			if (m.getId() != null && existingMap.containsKey(m.getId())) {
				target = existingMap.get(m.getId());
				touchedIds.add(m.getId());
			} else {
				target = new ExecutiveMember();
				target.setGroupTitle(groupTitle);
			}

			target.setRole(m.getRole().trim());
			target.setGeneration(m.getGeneration().trim());
			target.setDepartment(m.getDepartment().trim());
			target.setName(m.getName().trim());
			target.setImageUrl(m.getImageUrl());
			target.setOrderIndex(m.getOrderIndex());
			target.setActive(m.getActive() == null ? true : m.getActive());

			executiveMemberRepository.save(target);
		}

		for (ExecutiveMember e : existing) {
			if (e.getId() != null && !touchedIds.contains(e.getId())) {
				e.setActive(false);
			}
		}
	}

	@Transactional
	public void deleteOne(Long id) {
		executiveMemberRepository.deleteById(id);
	}

	private List<ExecutiveGroupResponse> groupize(List<ExecutiveMember> list) {
		Map<String, List<ExecutiveMember>> grouped = list.stream()
			.collect(Collectors.groupingBy(
				ExecutiveMember::getGroupTitle,
				LinkedHashMap::new,
				Collectors.toList()
			));

		List<ExecutiveGroupResponse> result = new ArrayList<>();

		for (Map.Entry<String, List<ExecutiveMember>> entry : grouped.entrySet()) {
			List<ExecutiveMemberResponse> members = entry.getValue().stream()
				.sorted(Comparator.comparingInt(ExecutiveMember::getOrderIndex).thenComparing(ExecutiveMember::getId))
				.map(this::toResponse)
				.toList();

			result.add(ExecutiveGroupResponse.builder()
				.title(entry.getKey())
				.members(members)
				.build());
		}
		return result;
	}

	private ExecutiveMemberResponse toResponse(ExecutiveMember e) {
		return ExecutiveMemberResponse.builder()
			.id(e.getId())
			.role(e.getRole())
			.generation(e.getGeneration())
			.department(e.getDepartment())
			.name(e.getName())
			.imageUrl(e.getImageUrl())
			.orderIndex(e.getOrderIndex())
			.build();
	}
}
