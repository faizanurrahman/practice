package com.practice.todo.modules.presence.infrastructure.scheduler;

import com.practice.todo.modules.presence.application.TaskPresenceService;
import com.practice.todo.modules.presence.application.UserOnlineStatusService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PresenceCleanupScheduler {

	private static final long STALE_AFTER_MS = TimeUnit.MINUTES.toMillis(5);

	private final TaskPresenceService taskPresenceService;
	private final UserOnlineStatusService userOnlineStatusService;

	@Scheduled(fixedDelayString = "${app.presence.cleanup-delay-ms:60000}")
	public void evictStalePresence() {
		try {
			taskPresenceService.evictStaleMembers(STALE_AFTER_MS);
			userOnlineStatusService.evictStaleMembers(STALE_AFTER_MS);
		} catch (RuntimeException ex) {
			log.error("presence cleanup run failed", ex);
		}
	}
}
