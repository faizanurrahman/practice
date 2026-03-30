package com.practice.todo.security;

import com.practice.todo.modules.user.application.port.UserRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepositoryPort userRepository;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository
				.findByEmailIgnoreCase(username)
				.map(UserPrincipal::new)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	@Transactional(readOnly = true)
	public UserDetails loadUserById(UUID id) {
		return userRepository
				.findById(id)
				.map(UserPrincipal::new)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}
}
