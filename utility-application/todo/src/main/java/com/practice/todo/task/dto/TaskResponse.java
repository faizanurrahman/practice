package com.practice.todo.task.dto;

import com.practice.todo.task.TaskStatus;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TaskResponse {

	UUID id;
	UUID projectId;
	UUID parentTaskId;
	String title;
	String content;
	TaskStatus status;
	Instant dueAt;
	Instant effectiveDueAt;
	int sortOrder;
	Instant createdAt;
	Instant updatedAt;
}
