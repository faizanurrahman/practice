package com.practice.todo.security;

import com.practice.todo.modules.user.domain.User;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class UserPrincipal implements UserDetails {

	private final UUID id;
	private final String email;
	private final String passwordHash;

	public UserPrincipal(User user) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.passwordHash = user.getPasswordHash();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_USER"));
	}

	@Override
	public String getPassword() {
		return passwordHash;
	}

	@Override
	public String getUsername() {
		return email;
	}
}
