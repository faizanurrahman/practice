package com.practice.todo.modules.user.application;

import com.practice.todo.modules.user.application.port.UserAuthPort;
import com.practice.todo.modules.user.application.port.UserLookupPort;
import com.practice.todo.modules.user.application.port.UserRepositoryPort;
import com.practice.todo.modules.user.domain.User;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserLookupPort, UserAuthPort {

	private final UserRepositoryPort userRepository;

	@Override
	@Transactional(readOnly = true)
	public User getById(UUID id) {
		return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
	}

	@Transactional
	public User completeOnboarding(UUID userId) {
		User u = getById(userId);
		u.setOnboardingCompletedAt(Instant.now());
		return userRepository.save(u);
	}

	@Transactional
	public User updateProfile(UUID userId, String displayName) {
		User u = getById(userId);
		if (displayName != null && !displayName.isBlank()) {
			u.setDisplayName(displayName.trim());
		}
		return userRepository.save(u);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<User> findByEmailIgnoreCase(String email) {
		return userRepository.findByEmailIgnoreCase(email);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByEmailIgnoreCase(String email) {
		return userRepository.existsByEmailIgnoreCase(email);
	}

	@Override
	@Transactional
	public User save(User user) {
		return userRepository.save(user);
	}
}
