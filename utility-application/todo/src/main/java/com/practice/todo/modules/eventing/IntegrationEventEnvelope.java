package com.practice.todo.modules.eventing;

import java.time.Instant;
import java.util.UUID;

public record IntegrationEventEnvelope(
		UUID id,
		String type,
		int version,
		Instant occurredAt,
		String source,
		String partitionKey,
		Object payload
) {}
