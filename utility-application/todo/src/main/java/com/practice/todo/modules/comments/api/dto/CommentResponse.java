package com.practice.todo.modules.comments.api.dto;

import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
		UUID id,
		UUID blockId,
		UUID taskId,
		UUID authorUserId,
		UUID parentCommentId,
		String body,
		String path,
		int depth,
		Instant createdAt,
		Instant updatedAt) {}
