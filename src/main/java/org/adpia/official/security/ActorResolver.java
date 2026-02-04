package org.adpia.official.security;

import org.adpia.official.domain.member.MemberRole;
import org.adpia.official.domain.recruit.service.RecruitService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ActorResolver {

	public RecruitService.Actor resolveOrGuest() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
			return RecruitService.Actor.guest();
		}

		Object principalObj = auth.getPrincipal();
		if ("anonymousUser".equals(principalObj)) {
			return RecruitService.Actor.guest();
		}

		if (!(principalObj instanceof MemberPrincipal principal)) {
			return RecruitService.Actor.guest();
		}

		MemberRole role = auth.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.map(this::safeRole)
			.filter(r -> r != null)
			.findFirst()
			.orElse(MemberRole.ROLE_USER);

		return new RecruitService.Actor(
			principal.getId(),
			principal.getUsername(),
			role
		);
	}

	private MemberRole safeRole(String authority) {
		try {
			return MemberRole.valueOf(authority);
		} catch (Exception e) {
			return null;
		}
	}
}
