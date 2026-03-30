package com.practice.todo.modules.task.application.port;

import com.practice.todo.modules.task.domain.TaskMember;
import com.practice.todo.modules.task.domain.TaskMemberRole;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskMemberRepositoryPort {

	List<TaskMember> findByTaskIdOrderByJoinedAtAsc(UUID taskId);

	Optional<TaskMember> findByTaskIdAndUserId(UUID taskId, UUID userId);

	boolean existsByTaskIdAndUserIdAndRole(UUID taskId, UUID userId, TaskMemberRole role);

	void deleteByTask_IdAndUserId(UUID taskId, UUID userId);

	Optional<TaskMember> findFirstByTask_IdAndRoleOrderByJoinedAtAsc(UUID taskId, TaskMemberRole role);

	TaskMember save(TaskMember member);
}
