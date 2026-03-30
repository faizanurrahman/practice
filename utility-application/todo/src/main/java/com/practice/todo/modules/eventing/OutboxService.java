package com.practice.todo.modules.eventing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxService {

	private final OutboxEventRepository outboxEventRepository;
	private final ObjectMapper objectMapper;

	@Transactional
	public void enqueue(String topic, String partitionKey, IntegrationEvent event) {
		UUID eventId = UUID.randomUUID();
		Instant now = Instant.now();
		IntegrationEventEnvelope envelope = new IntegrationEventEnvelope(
				eventId, event.type(), event.version(), now, event.source(), partitionKey, event.payload());
		String payloadJson;
		try {
			payloadJson = objectMapper.writeValueAsString(envelope);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Failed to serialize event envelope", e);
		}
		OutboxEvent e = new OutboxEvent();
		e.setTopic(topic);
		e.setPartitionKey(partitionKey);
		e.setEventId(eventId);
		e.setEventType(event.type());
		e.setEventVersion(event.version());
		e.setSource(event.source());
		e.setPayloadJson(payloadJson);
		outboxEventRepository.save(e);
	}

	@Transactional
	public void enqueue(String topic, String partitionKey, String eventType, Object payload, String source) {
		enqueue(topic, partitionKey, new IntegrationEvent(eventType, 1, source, payload));
	}
}
