package com.practice.todo.task.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class TaskReorderRequest {

	@NotEmpty
	private List<UUID> orderedTaskIds;
}
