package com.practice.todo.modules.task.infrastructure.persistence;

import com.practice.todo.modules.task.application.port.TaskMemberRepositoryPort;
import com.practice.todo.modules.task.domain.TaskMember;
import com.practice.todo.modules.task.domain.TaskMemberRole;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskMemberJpaRepository
		extends JpaRepository<TaskMember, UUID>, TaskMemberRepositoryPort {

	@Query("select m from TaskMember m where m.task.id = :taskId order by m.joinedAt asc")
	List<TaskMember> findByTaskIdOrderByJoinedAtAsc(@Param("taskId") UUID taskId);

	@Query("select m from TaskMember m where m.task.id = :taskId and m.userId = :userId")
	Optional<TaskMember> findByTaskIdAndUserId(@Param("taskId") UUID taskId, @Param("userId") UUID userId);

	@Query(
			"select case when count(m) > 0 then true else false end from TaskMember m where m.task.id = :taskId and m.userId = :userId and m.role = :role")
	boolean existsByTaskIdAndUserIdAndRole(
			@Param("taskId") UUID taskId, @Param("userId") UUID userId, @Param("role") TaskMemberRole role);

	void deleteByTask_IdAndUserId(UUID taskId, UUID userId);

	Optional<TaskMember> findFirstByTask_IdAndRoleOrderByJoinedAtAsc(
			UUID taskId, TaskMemberRole role);
}
