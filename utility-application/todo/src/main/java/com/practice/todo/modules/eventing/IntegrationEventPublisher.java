package com.practice.todo.modules.eventing;

public interface IntegrationEventPublisher {

	void publish(String topic, String partitionKey, String eventType, String payloadJson);
}
