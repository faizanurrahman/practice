package com.practice.todo.modules.task.api.workplatform;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record WpCreateTaskRequest(
		@NotNull(message = "projectId is required") UUID projectId,
		@NotBlank(message = "title is required") String title,
		String description,
		String priority,
		Instant dueAt,
		Instant startAt) {}
