package org.adpia.official.security;

import org.adpia.official.domain.member.MemberRole;
import org.adpia.official.domain.recruit.service.RecruitService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public final class ActorResolver {

	private ActorResolver() {}

	public RecruitService.Actor resolveOrGuest() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null || !auth.isAuthenticated()
			|| auth.getPrincipal() == null
			|| "anonymousUser".equals(auth.getPrincipal())) {
			return RecruitService.Actor.guest();
		}

		Object principalObj = auth.getPrincipal();

		if (!(principalObj instanceof MemberPrincipal principal)) {
			return RecruitService.Actor.guest();
		}

		MemberRole role = auth.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.map(MemberRole::valueOf)
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("권한 정보가 없습니다."));

		return new RecruitService.Actor(
			principal.getId(),
			principal.getUsername(),
			role
		);
	}
}
