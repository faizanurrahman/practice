package com.practice.todo.modules.presence.application;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserOnlineStatusService {

	private final UserOnlineStatusPort userOnlineStatusPort;

	public void markOnline(UUID userId) {
		Objects.requireNonNull(userId, "userId");
		userOnlineStatusPort.markOnline(userId);
	}

	public void markOffline(UUID userId) {
		Objects.requireNonNull(userId, "userId");
		userOnlineStatusPort.markOffline(userId);
	}

	public Set<UUID> getOnlineUsers() {
		return userOnlineStatusPort.getOnlineUsers();
	}

	public void heartbeat(UUID userId) {
		Objects.requireNonNull(userId, "userId");
		userOnlineStatusPort.heartbeat(userId);
	}

	public void evictStaleMembers(long maxIdleMillis) {
		if (maxIdleMillis <= 0) {
			log.warn("evictStaleMembers ignored: maxIdleMillis must be positive");
			return;
		}
		userOnlineStatusPort.evictStaleMembers(maxIdleMillis);
	}
}
