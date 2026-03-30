package com.practice.todo.modules.eventing;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnBean(KafkaTemplate.class)
public class KafkaIntegrationEventPublisher implements IntegrationEventPublisher {

	private final KafkaTemplate<String, String> kafkaTemplate;

	@Override
	public void publish(String topic, String partitionKey, String eventType, String payloadJson) {
		kafkaTemplate.send(topic, partitionKey != null ? partitionKey : "", payloadJson);
	}
}
