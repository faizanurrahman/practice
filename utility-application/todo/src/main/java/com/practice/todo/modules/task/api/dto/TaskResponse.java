package com.practice.todo.modules.task.api.dto;

import com.practice.todo.modules.task.domain.TaskStatus;
import java.time.Instant;
import java.util.List;
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
	String priority;
	Instant dueAt;
	Instant startAt;
	UUID assigneeUserId;
	List<String> labels;
	Instant effectiveDueAt;
	int sortOrder;
	String path;
	int depth;
	long version;
	Instant createdAt;
	Instant updatedAt;
}
