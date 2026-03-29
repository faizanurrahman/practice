package com.practice.todo.user;

import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

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
}
