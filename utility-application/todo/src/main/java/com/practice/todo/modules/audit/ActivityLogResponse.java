package com.practice.todo.modules.audit;

import java.time.Instant;
import java.util.UUID;

public record ActivityLogResponse(
		UUID id,
		String entityType,
		UUID entityId,
		UUID userId,
		String action,
		String details,
		UUID workspaceId,
		Instant createdAt) {}
