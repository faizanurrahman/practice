package com.practice.todo.modules.task.application.port;

import com.practice.todo.modules.task.domain.TaskLabel;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface TaskLabelRepositoryPort {

	List<TaskLabel> findByTaskId(UUID taskId);

	List<TaskLabel> findByTaskIdIn(Collection<UUID> taskIds);

	void deleteByTaskId(UUID taskId);

	void deleteByTaskIdAndLabelId(UUID taskId, UUID labelId);

	TaskLabel save(TaskLabel taskLabel);
}
