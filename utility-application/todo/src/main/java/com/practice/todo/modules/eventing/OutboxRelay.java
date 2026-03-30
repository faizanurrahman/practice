package com.practice.todo.modules.eventing;

import com.practice.todo.config.AppProperties;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxRelay {

	private final OutboxEventRepository outboxEventRepository;
	private final IntegrationEventPublisher integrationEventPublisher;
	private final AppProperties appProperties;

	@Scheduled(fixedDelayString = "${app.outbox-relay-ms:2000}")
	@Transactional
	public void publishBatch() {
		int batchSize = appProperties.getOutboxBatchSize();
		List<OutboxEvent> batch = outboxEventRepository.findBatchForPublish(
				Instant.now(), PageRequest.of(0, batchSize));
		if (batch.isEmpty()) {
			return;
		}
		Instant now = Instant.now();
		for (OutboxEvent e : batch) {
			try {
				integrationEventPublisher.publish(
						e.getTopic(), e.getPartitionKey(), e.getEventType(), e.getPayloadJson());
				e.setPublishedAt(now);
				e.setLastError(null);
				e.setNextAttemptAt(null);
			} catch (Exception ex) {
				int attempts = e.getAttempts() + 1;
				e.setAttempts(attempts);
				e.setLastError(ex.getMessage());
				e.setNextAttemptAt(now.plusSeconds(backoffSeconds(attempts)));
				log.warn("Outbox publish failed id={} attempts={}: {}", e.getId(), attempts, ex.getMessage());
			}
		}
		outboxEventRepository.saveAll(batch);
	}

	private long backoffSeconds(int attempts) {
		long base = Math.max(1, appProperties.getOutboxBackoffSeconds());
		int exp = Math.min(attempts - 1, 6);
		long backoff = base * (1L << exp);
		return Math.min(backoff, 3600);
	}
}
