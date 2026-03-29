package com.practice.todo.task.dto;

import com.practice.todo.task.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
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

	private TaskStatus status;
}
