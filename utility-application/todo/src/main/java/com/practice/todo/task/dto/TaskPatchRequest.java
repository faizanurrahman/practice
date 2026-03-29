package com.practice.todo.task.dto;

import com.practice.todo.task.TaskStatus;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.Data;

@Data
public class TaskPatchRequest {

	@Size(max = 500)
	private String title;

	@Size(max = 50_000)
	private String content;

	private TaskStatus status;

	private Instant dueAt;

	private Boolean clearDueAt;
}
