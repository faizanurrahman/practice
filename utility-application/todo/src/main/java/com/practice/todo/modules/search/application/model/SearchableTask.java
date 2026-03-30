package com.practice.todo.modules.search.application.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SearchableTask(
		UUID id,
		UUID projectId,
		String title,
		String status,
		String priority,
		List<String> labels,
		List<UUID> memberUserIds,
		Instant createdAt) {}
