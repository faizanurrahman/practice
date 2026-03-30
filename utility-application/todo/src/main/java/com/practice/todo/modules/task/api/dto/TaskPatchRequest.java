package com.practice.todo.modules.task.api.dto;

import com.practice.todo.modules.task.domain.TaskStatus;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class TaskPatchRequest {

	@Size(max = 500)
	private String title;

	@Size(max = 50_000)
	private String content;

	private TaskStatus status;

	private String priority;

	private Instant dueAt;

	private Instant startAt;

	private Boolean clearDueAt;

	private Boolean clearStartAt;

	private UUID assigneeUserId;

	private Boolean clearAssignee;

	@Size(max = 20)
	private List<String> labels;

	private Boolean clearLabels;
}
