package com.practice.todo.modules.eventing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(KafkaIntegrationEventPublisher.class)
@Slf4j
public class LoggingIntegrationEventPublisher implements IntegrationEventPublisher {

	@Override
	public void publish(String topic, String partitionKey, String eventType, String payloadJson) {
		log.info("Integration event topic={} type={} key={} payload={}", topic, eventType, partitionKey, payloadJson);
	}
}
