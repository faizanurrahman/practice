package com.practice.todo.modules.presence.infrastructure.memory;

import com.practice.todo.modules.presence.application.UserOnlineStatusPort;
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
public class InMemoryUserOnlineStatusAdapter implements UserOnlineStatusPort {

	private static final long ACTIVE_WINDOW_MS = 300_000L;

	private final Map<UUID, Long> lastSeen = new ConcurrentHashMap<>();

	@Override
	public void markOnline(UUID userId) {
		heartbeat(userId);
	}

	@Override
	public void markOffline(UUID userId) {
		lastSeen.remove(userId);
	}

	@Override
	public boolean isOnline(UUID userId) {
		Long ts = lastSeen.get(userId);
		return ts != null && ts >= nowMillis() - ACTIVE_WINDOW_MS;
	}

	@Override
	public Set<UUID> getOnlineUsers() {
		long cutoff = nowMillis() - ACTIVE_WINDOW_MS;
		Set<UUID> ids = new HashSet<>();
		lastSeen.forEach((id, ts) -> {
			if (ts >= cutoff) {
				ids.add(id);
			}
		});
		return Collections.unmodifiableSet(ids);
	}

	@Override
	public void heartbeat(UUID userId) {
		lastSeen.put(userId, nowMillis());
	}

	@Override
	public void evictStaleMembers(long maxIdleMillis) {
		if (maxIdleMillis <= 0) {
			return;
		}
		long cutoff = nowMillis() - maxIdleMillis;
		lastSeen.entrySet().removeIf(e -> e.getValue() < cutoff);
	}

	private static long nowMillis() {
		return System.currentTimeMillis();
	}
}
