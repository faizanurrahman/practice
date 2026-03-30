package com.practice.todo.modules.presence.application;

import java.util.Set;
import java.util.UUID;

public interface UserOnlineStatusPort {

	void markOnline(UUID userId);

	void markOffline(UUID userId);

	boolean isOnline(UUID userId);

	Set<UUID> getOnlineUsers();

	void heartbeat(UUID userId);

	void evictStaleMembers(long maxIdleMillis);
}
