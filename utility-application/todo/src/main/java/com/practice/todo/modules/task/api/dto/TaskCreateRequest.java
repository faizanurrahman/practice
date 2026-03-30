package com.practice.todo.modules.task.api.dto;

import com.practice.todo.modules.task.domain.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class TaskCreateRequest {

	@NotBlank
	@Size(max = 500)
	private String title;

	@Size(max = 50_000)
	private String content;

	private UUID parentTaskId;

	private Instant dueAt;

	private Instant startAt;

	private UUID assigneeUserId;

	/** Stored as work-platform style: LOW, MEDIUM, HIGH, URGENT */
	private String priority;

	@Size(max = 20)
	private List<String> labels;

	private TaskStatus status;
}
