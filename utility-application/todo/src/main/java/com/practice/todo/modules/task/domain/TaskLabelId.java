package com.practice.todo.modules.task.domain;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskLabelId implements Serializable {

	private UUID taskId;
	private UUID labelId;
}
