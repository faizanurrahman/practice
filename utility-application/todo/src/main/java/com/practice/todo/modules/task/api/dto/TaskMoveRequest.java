package com.practice.todo.modules.task.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.UUID;
import lombok.Data;

@Data
public class TaskMoveRequest {

	/** Null means move to root of the project */
	@JsonAlias("newParentId")
	private UUID newParentTaskId;
}
