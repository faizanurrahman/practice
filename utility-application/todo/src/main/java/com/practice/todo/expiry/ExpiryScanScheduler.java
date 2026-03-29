package com.practice.todo.expiry;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpiryScanScheduler {

	private final ExpiryScanService expiryScanService;

	@Scheduled(fixedDelayString = "${app.expiry-scan-ms:60000}")
	public void tick() {
		try {
			expiryScanService.scanAndPublish(Instant.now());
		} catch (Exception e) {
			log.warn("Expiry scan failed: {}", e.getMessage());
		}
	}
}
