package com.practice.todo.modules.presence.infrastructure.redis;

import com.practice.todo.modules.presence.application.TaskPresencePort;
import com.practice.todo.modules.presence.domain.TaskPresence;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(StringRedisTemplate.class)
@RequiredArgsConstructor
@Slf4j
public class RedisTaskPresenceAdapter implements TaskPresencePort {

	private static final String ZSET_PREFIX = "todo:task:presence:";
	private static final long ACTIVE_WINDOW_MS = 300_000L;

	private final StringRedisTemplate stringRedisTemplate;

	@Override
	public void join(UUID taskId, UUID userId, String displayName) {
		String zkey = TodoPresenceRedisKeys.taskPresenceZset(taskId.toString());
		String namesKey = TodoPresenceRedisKeys.taskPresenceNames(taskId.toString());
		long now = nowMillis();
		String member = userId.toString();
		stringRedisTemplate.opsForZSet().add(zkey, member, now);
		stringRedisTemplate.opsForHash().put(namesKey, member, displayName != null ? displayName : "");
	}

	@Override
	public void leave(UUID taskId, UUID userId) {
		String zkey = TodoPresenceRedisKeys.taskPresenceZset(taskId.toString());
		String namesKey = TodoPresenceRedisKeys.taskPresenceNames(taskId.toString());
		String member = userId.toString();
		stringRedisTemplate.opsForZSet().remove(zkey, member);
		stringRedisTemplate.opsForHash().delete(namesKey, member);
	}

	@Override
	public Set<TaskPresence> getActiveUsers(UUID taskId) {
		String zkey = TodoPresenceRedisKeys.taskPresenceZset(taskId.toString());
		String namesKey = TodoPresenceRedisKeys.taskPresenceNames(taskId.toString());
		long cutoff = nowMillis() - ACTIVE_WINDOW_MS;
		Set<ZSetOperations.TypedTuple<String>> tuples =
				stringRedisTemplate.opsForZSet().rangeByScoreWithScores(zkey, (double) cutoff, Double.MAX_VALUE);
		if (tuples == null || tuples.isEmpty()) {
			return Set.of();
		}
		Set<TaskPresence> out = new HashSet<>();
		for (ZSetOperations.TypedTuple<String> tuple : tuples) {
			String member = tuple.getValue();
			Double score = tuple.getScore();
			if (member == null || score == null) {
				continue;
			}
			try {
				UUID uid = UUID.fromString(member);
				Object rawName = stringRedisTemplate.opsForHash().get(namesKey, member);
				String displayName = rawName != null ? rawName.toString() : "";
				out.add(new TaskPresence(taskId, uid, displayName, Instant.ofEpochMilli(score.longValue())));
			} catch (IllegalArgumentException ex) {
				log.warn("skip invalid task presence member zsetKey={} member={}", zkey, member);
			}
		}
		return Collections.unmodifiableSet(out);
	}

	@Override
	public void heartbeat(UUID taskId, UUID userId) {
		String zkey = TodoPresenceRedisKeys.taskPresenceZset(taskId.toString());
		String member = userId.toString();
		Double score = stringRedisTemplate.opsForZSet().score(zkey, member);
		if (score == null) {
			log.debug("heartbeat ignored: user not in task zset taskId={} userId={}", taskId, userId);
			return;
		}
		stringRedisTemplate.opsForZSet().add(zkey, member, nowMillis());
	}

	@Override
	public void evictStaleMembers(long maxIdleMillis) {
		if (maxIdleMillis <= 0) {
			return;
		}
		long maxScore = nowMillis() - maxIdleMillis;
		ScanOptions options = ScanOptions.scanOptions().match(ZSET_PREFIX + "*").count(128).build();
		try (Cursor<String> cursor = stringRedisTemplate.scan(options)) {
			while (cursor.hasNext()) {
				String key = cursor.next();
				if (key == null || key.contains(":names:")) {
					continue;
				}
				if (!key.startsWith(ZSET_PREFIX)) {
					continue;
				}
				evictStaleFromZset(key, maxScore);
			}
		} catch (RuntimeException ex) {
			log.error("scan task presence keys failed", ex);
			throw ex;
		}
	}

	private void evictStaleFromZset(String zkey, long maxScoreInclusive) {
		Set<String> stale = stringRedisTemplate.opsForZSet().rangeByScore(zkey, 0d, (double) maxScoreInclusive);
		if (stale == null || stale.isEmpty()) {
			return;
		}
		UUID taskId;
		try {
			taskId = UUID.fromString(zkey.substring(ZSET_PREFIX.length()));
		} catch (RuntimeException ex) {
			log.warn("could not parse taskId from redis key {}", zkey);
			return;
		}
		String namesKey = TodoPresenceRedisKeys.taskPresenceNames(taskId.toString());
		stringRedisTemplate.opsForZSet().remove(zkey, stale.toArray(new Object[0]));
		for (String member : stale) {
			stringRedisTemplate.opsForHash().delete(namesKey, member);
		}
		if (!stale.isEmpty()) {
			log.debug("evicted {} stale presence entries from {}", stale.size(), zkey);
		}
	}

	private static long nowMillis() {
		return System.currentTimeMillis();
	}
}
