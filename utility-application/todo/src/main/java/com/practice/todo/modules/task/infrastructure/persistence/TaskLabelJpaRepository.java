package com.practice.todo.modules.task.infrastructure.persistence;

import com.practice.todo.modules.task.application.port.TaskLabelRepositoryPort;
import com.practice.todo.modules.task.domain.TaskLabel;
import com.practice.todo.modules.task.domain.TaskLabelId;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskLabelJpaRepository extends JpaRepository<TaskLabel, TaskLabelId>, TaskLabelRepositoryPort {

	@Query("select tl from TaskLabel tl join fetch tl.label where tl.taskId = :taskId")
	List<TaskLabel> findByTaskId(@Param("taskId") UUID taskId);

	@Query("select tl from TaskLabel tl join fetch tl.label where tl.taskId in :taskIds")
	List<TaskLabel> findByTaskIdIn(@Param("taskIds") Collection<UUID> taskIds);

	void deleteByTaskId(UUID taskId);

	void deleteByTaskIdAndLabelId(UUID taskId, UUID labelId);
}
