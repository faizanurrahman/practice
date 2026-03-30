package com.practice.todo.modules.presence.infrastructure.websocket;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class TaskPresenceSubscriptionTracker {

	private final ConcurrentHashMap<String, Set<UUID>> sessionToTasks = new ConcurrentHashMap<>();

	public void track(String sessionId, UUID taskId) {
		if (sessionId == null || taskId == null) {
			return;
		}
		sessionToTasks.computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet()).add(taskId);
	}

	public void untrack(String sessionId, UUID taskId) {
		if (sessionId == null || taskId == null) {
			return;
		}
		sessionToTasks.computeIfPresent(sessionId, (k, v) -> {
			v.remove(taskId);
			return v.isEmpty() ? null : v;
		});
	}

	public Set<UUID> drain(String sessionId) {
		if (sessionId == null) {
			return Set.of();
		}
		Set<UUID> tasks = sessionToTasks.remove(sessionId);
		return tasks == null ? Set.of() : Set.copyOf(tasks);
	}
}
