package com.practice.todo.modules.task.application.port;

import com.practice.todo.modules.task.domain.JoinRequestStatus;
import com.practice.todo.modules.task.domain.TaskJoinRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskJoinRequestRepositoryPort {

	List<TaskJoinRequest> findByTaskIdAndStatus(UUID taskId, JoinRequestStatus status);

	Optional<TaskJoinRequest> findByIdWithTask(UUID id);

	boolean existsByTask_IdAndRequesterIdAndStatus(
			UUID taskId, UUID requesterId, JoinRequestStatus status);

	TaskJoinRequest save(TaskJoinRequest request);
}
