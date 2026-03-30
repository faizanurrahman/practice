package com.practice.todo.modules.content.api.dto;

import java.time.Instant;
import java.util.UUID;

public record BlockResponse(
		UUID id,
		UUID taskId,
		UUID ownerUserId,
		String blockType,
		String content,
		int sortOrder,
		Instant createdAt,
		Instant updatedAt) {}
