package org.adpia.official.security;

import lombok.Getter;
import org.adpia.official.domain.member.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class MemberPrincipal implements UserDetails {

	private final Long id;
	private final String email;
	private final String password;
	private final Collection<? extends GrantedAuthority> authorities;
	private final boolean active;

	public MemberPrincipal(Member member) {
		this.id = member.getId();
		this.email = member.getEmail();
		this.password = member.getPassword();
		this.active = Boolean.TRUE.equals(member.getActive());
		this.authorities = List.of(new SimpleGrantedAuthority(member.getRole().name()));
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	// username 자리에 email 사용
	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override public boolean isAccountNonExpired() { return true; }
	@Override public boolean isAccountNonLocked() { return active; }
	@Override public boolean isCredentialsNonExpired() { return true; }
	@Override public boolean isEnabled() { return active; }
}
