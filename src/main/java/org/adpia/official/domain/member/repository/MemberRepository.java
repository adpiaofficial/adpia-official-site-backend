package org.adpia.official.domain.member.repository;

import java.util.Optional;

import org.adpia.official.domain.member.Member;
import org.adpia.official.domain.member.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByRole(MemberRole role);


}
