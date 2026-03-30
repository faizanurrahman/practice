package com.practice.todo.modules.presence.application;

import com.practice.todo.modules.presence.domain.TaskPresence;
import java.util.Set;
import java.util.UUID;

public interface TaskPresencePort {

	void join(UUID taskId, UUID userId, String displayName);

	void leave(UUID taskId, UUID userId);

	Set<TaskPresence> getActiveUsers(UUID taskId);

	void heartbeat(UUID taskId, UUID userId);

	void evictStaleMembers(long maxIdleMillis);
}
