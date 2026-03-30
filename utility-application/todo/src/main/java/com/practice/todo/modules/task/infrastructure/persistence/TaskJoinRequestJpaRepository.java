package com.practice.todo.modules.task.infrastructure.persistence;

import com.practice.todo.modules.task.application.port.TaskJoinRequestRepositoryPort;
import com.practice.todo.modules.task.domain.JoinRequestStatus;
import com.practice.todo.modules.task.domain.TaskJoinRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskJoinRequestJpaRepository
		extends JpaRepository<TaskJoinRequest, UUID>, TaskJoinRequestRepositoryPort {

	@Query("select r from TaskJoinRequest r where r.task.id = :taskId and r.status = :status order by r.requestedAt asc")
	List<TaskJoinRequest> findByTaskIdAndStatus(
			@Param("taskId") UUID taskId, @Param("status") JoinRequestStatus status);

	@Query("select r from TaskJoinRequest r join fetch r.task where r.id = :id")
	Optional<TaskJoinRequest> findByIdWithTask(@Param("id") UUID id);

	boolean existsByTask_IdAndRequesterIdAndStatus(
			UUID taskId, UUID requesterId, JoinRequestStatus status);

	Optional<TaskJoinRequest> findByIdAndRequesterId(UUID id, UUID requesterId);
}
