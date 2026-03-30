package com.practice.todo.modules.presence.infrastructure.memory;

import com.practice.todo.modules.presence.application.TaskPresencePort;
import com.practice.todo.modules.presence.domain.TaskPresence;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(StringRedisTemplate.class)
public class InMemoryTaskPresenceAdapter implements TaskPresencePort {

	private static final long ACTIVE_WINDOW_MS = 300_000L;

	private final Map<UUID, Map<UUID, Long>> taskToUserTs = new ConcurrentHashMap<>();
	private final Map<UUID, Map<UUID, String>> taskToUserNames = new ConcurrentHashMap<>();

	@Override
	public void join(UUID taskId, UUID userId, String displayName) {
		long now = nowMillis();
		taskToUserTs.computeIfAbsent(taskId, k -> new ConcurrentHashMap<>()).put(userId, now);
		taskToUserNames
				.computeIfAbsent(taskId, k -> new ConcurrentHashMap<>())
				.put(userId, displayName != null ? displayName : "");
	}

	@Override
	public void leave(UUID taskId, UUID userId) {
		Map<UUID, Long> ts = taskToUserTs.get(taskId);
		if (ts != null) {
			ts.remove(userId);
		}
		Map<UUID, String> names = taskToUserNames.get(taskId);
		if (names != null) {
			names.remove(userId);
		}
	}

	@Override
	public Set<TaskPresence> getActiveUsers(UUID taskId) {
		Map<UUID, Long> ts = taskToUserTs.get(taskId);
		if (ts == null || ts.isEmpty()) {
			return Set.of();
		}
		long cutoff = nowMillis() - ACTIVE_WINDOW_MS;
		Map<UUID, String> names = taskToUserNames.getOrDefault(taskId, Map.of());
		Set<TaskPresence> out = new HashSet<>();
		ts.forEach((userId, last) -> {
			if (last >= cutoff) {
				String dn = names.getOrDefault(userId, "");
				out.add(new TaskPresence(taskId, userId, dn, Instant.ofEpochMilli(last)));
			}
		});
		return Collections.unmodifiableSet(out);
	}

	@Override
	public void heartbeat(UUID taskId, UUID userId) {
		Map<UUID, Long> ts = taskToUserTs.get(taskId);
		if (ts == null || !ts.containsKey(userId)) {
			return;
		}
		ts.put(userId, nowMillis());
	}

	@Override
	public void evictStaleMembers(long maxIdleMillis) {
		if (maxIdleMillis <= 0) {
			return;
		}
		long cutoff = nowMillis() - maxIdleMillis;
		taskToUserTs.forEach((taskId, users) -> users.entrySet().removeIf(e -> e.getValue() < cutoff));
	}

	private static long nowMillis() {
		return System.currentTimeMillis();
	}
}
