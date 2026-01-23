package org.adpia.official.security;

import org.adpia.official.domain.member.MemberRole;
import org.adpia.official.domain.recruit.service.RecruitService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public final class ActorResolver {

	private ActorResolver() {}

	public static RecruitService.Actor resolveOrGuest() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null || !auth.isAuthenticated()
			|| auth.getPrincipal() == null
			|| "anonymousUser".equals(auth.getPrincipal())) {
			return RecruitService.Actor.guest();
		}

		Object principalObj = auth.getPrincipal();

		// ✅ 여기: principal은 MemberPrincipal이어야 한다
		if (!(principalObj instanceof MemberPrincipal principal)) {
			// 필터에서 principal을 다르게 넣는 경우 방어
			return RecruitService.Actor.guest();
		}

		MemberRole role = auth.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority) // "ROLE_SUPER_ADMIN"
			.map(MemberRole::valueOf)
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("권한 정보가 없습니다."));

		return new RecruitService.Actor(
			principal.getId(),
			principal.getUsername(), // 여기 username=email
			role
		);
	}
}
