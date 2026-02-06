package org.adpia.official.domain.executive.repository;

import org.adpia.official.domain.executive.ExecutiveMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExecutiveMemberRepository extends JpaRepository<ExecutiveMember, Long> {

	List<ExecutiveMember> findAllByActiveTrueOrderByGroupTitleAscOrderIndexAscIdAsc();

	List<ExecutiveMember> findAllByOrderByGroupTitleAscOrderIndexAscIdAsc();
}
