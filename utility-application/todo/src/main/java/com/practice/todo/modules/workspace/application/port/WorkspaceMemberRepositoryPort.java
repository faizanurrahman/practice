package com.practice.todo.modules.workspace.application.port;

import com.practice.todo.modules.workspace.domain.WorkspaceMember;
import com.practice.todo.modules.workspace.domain.WorkspaceMemberId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceMemberRepositoryPort {

	List<WorkspaceMember> findByUserIdOrderByJoinedAtAsc(UUID userId);

	boolean existsByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

	Optional<WorkspaceMember> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

	WorkspaceMember save(WorkspaceMember member);

	void deleteById(WorkspaceMemberId id);
}
