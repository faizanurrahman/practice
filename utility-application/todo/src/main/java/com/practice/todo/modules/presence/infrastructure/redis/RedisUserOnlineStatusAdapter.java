package com.practice.todo.modules.presence.infrastructure.redis;

import com.practice.todo.modules.presence.application.UserOnlineStatusPort;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(StringRedisTemplate.class)
@RequiredArgsConstructor
@Slf4j
public class RedisUserOnlineStatusAdapter implements UserOnlineStatusPort {

	private static final long ACTIVE_WINDOW_MS = 300_000L;

	private final StringRedisTemplate stringRedisTemplate;

	@Override
	public void markOnline(UUID userId) {
		heartbeat(userId);
	}

	@Override
	public void markOffline(UUID userId) {
		stringRedisTemplate.opsForZSet().remove(TodoPresenceRedisKeys.USER_ONLINE, userId.toString());
	}

	@Override
	public boolean isOnline(UUID userId) {
		Double score = stringRedisTemplate.opsForZSet().score(TodoPresenceRedisKeys.USER_ONLINE, userId.toString());
		if (score == null) {
			return false;
		}
		return score >= nowMillis() - ACTIVE_WINDOW_MS;
	}

	@Override
	public Set<UUID> getOnlineUsers() {
		long cutoff = nowMillis() - ACTIVE_WINDOW_MS;
		Set<String> members = stringRedisTemplate
				.opsForZSet()
				.rangeByScore(TodoPresenceRedisKeys.USER_ONLINE, (double) cutoff, Double.MAX_VALUE);
		if (members == null || members.isEmpty()) {
			return Set.of();
		}
		Set<UUID> ids = new HashSet<>();
		for (String member : members) {
			try {
				ids.add(UUID.fromString(member));
			} catch (IllegalArgumentException ex) {
				log.warn("skip invalid user online member={}", member);
			}
		}
		return Collections.unmodifiableSet(ids);
	}

	@Override
	public void heartbeat(UUID userId) {
		stringRedisTemplate.opsForZSet().add(TodoPresenceRedisKeys.USER_ONLINE, userId.toString(), nowMillis());
	}

	@Override
	public void evictStaleMembers(long maxIdleMillis) {
		if (maxIdleMillis <= 0) {
			return;
		}
		long maxScore = nowMillis() - maxIdleMillis;
		Long removed = stringRedisTemplate
				.opsForZSet()
				.removeRangeByScore(TodoPresenceRedisKeys.USER_ONLINE, 0, maxScore);
		if (removed != null && removed > 0) {
			log.debug("evicted {} stale user:online entries", removed);
		}
	}

	private static long nowMillis() {
		return System.currentTimeMillis();
	}
}
