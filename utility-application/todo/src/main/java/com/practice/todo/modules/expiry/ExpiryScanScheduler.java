package com.practice.todo.modules.expiry;

import com.practice.todo.modules.jobs.ExpiryScanPayload;
import com.practice.todo.modules.jobs.JobQueuePort;
import com.practice.todo.modules.jobs.JobRequest;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpiryScanScheduler {

	private final JobQueuePort jobQueuePort;

	@Scheduled(fixedDelayString = "${app.expiry-scan-ms:60000}")
	public void tick() {
		try {
			jobQueuePort.enqueue(new JobRequest("expiry.scan", 1, new ExpiryScanPayload(Instant.now())));
		} catch (Exception e) {
			log.warn("Expiry scan enqueue failed: {}", e.getMessage());
		}
	}
}
