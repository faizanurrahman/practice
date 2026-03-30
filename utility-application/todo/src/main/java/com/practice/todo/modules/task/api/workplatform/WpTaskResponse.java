package com.practice.todo.modules.task.api.workplatform;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record WpTaskResponse(
		UUID id,
		UUID projectId,
		UUID parentTaskId,
		String title,
		String status,
		String priority,
		Instant dueAt,
		int sortOrder,
		String path,
		int depth,
		List<WpTaskMemberResponse> members,
		Instant createdAt) {}
