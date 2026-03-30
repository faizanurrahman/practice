package com.practice.todo.modules.task.application.port;

import com.practice.todo.modules.task.domain.Task;
import com.practice.todo.modules.task.domain.TaskStatus;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepositoryPort {

	List<Task> findRootsByProjectId(UUID projectId);

	List<Task> findChildren(UUID parentId);

	Optional<Task> findByIdAndDeletedAtIsNull(UUID id);

	Optional<Task> findByIdWithProject(UUID id);

	Optional<Task> findByIdWithProjectIncludingDeleted(UUID id);

	List<Task> findMyAssignedActive(UUID userId);

	List<UUID> findChildIds(UUID parentId);

	long countActiveIncompleteChildren(UUID parentId, TaskStatus done);

	List<Task> findAllActiveByProjectId(UUID projectId);

	List<Task> findActiveWithExplicitDueByProjectId(UUID projectId);

	List<Task> findAllByIdInWithProject(Collection<UUID> ids);

	void markDeleted(Collection<UUID> ids, Instant at);

	List<Task> findOverdueExplicitDue(Instant now, TaskStatus done);

	List<Task> findSubtreeByPathPrefix(UUID projectId, String pathPrefix);

	Task save(Task task);

	/** Batch persist without clashing with {@link org.springframework.data.repository.ListCrudRepository} erasure. */
	void saveAllTree(Iterable<Task> tasks);
}
