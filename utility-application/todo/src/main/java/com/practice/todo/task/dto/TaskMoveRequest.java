package com.practice.todo.task.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class TaskMoveRequest {

	/** Null means move to root of the project */
	private UUID newParentTaskId;
}
