package com.practice.todo.modules.workspace.infrastructure.persistence;

import com.practice.todo.modules.workspace.application.port.WorkspaceMemberRepositoryPort;
import com.practice.todo.modules.workspace.domain.WorkspaceMember;
import com.practice.todo.modules.workspace.domain.WorkspaceMemberId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceMemberJpaRepository extends JpaRepository<WorkspaceMember, WorkspaceMemberId>, WorkspaceMemberRepositoryPort {

	List<WorkspaceMember> findByUserIdOrderByJoinedAtAsc(UUID userId);

	boolean existsByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

	Optional<WorkspaceMember> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);
}
