package com.practice.todo.modules.workspace.application.policy;

import com.practice.todo.modules.iam.WorkspaceAccessPolicy;
import com.practice.todo.modules.workspace.application.port.WorkspaceMemberRepositoryPort;
import com.practice.todo.modules.workspace.domain.WorkspaceRole;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultWorkspaceAccessPolicy implements WorkspaceAccessPolicy {

	private final WorkspaceMemberRepositoryPort workspaceMemberRepository;

	@Override
	public boolean isMember(UUID userId, UUID workspaceId) {
		return workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId);
	}

	@Override
	public boolean isAdmin(UUID userId, UUID workspaceId) {
		return workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
				.map(m -> m.getRole() == WorkspaceRole.ADMIN || m.getRole() == WorkspaceRole.OWNER)
				.orElse(false);
	}

	@Override
	public boolean isOwner(UUID userId, UUID workspaceId) {
		return workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
				.map(m -> m.getRole() == WorkspaceRole.OWNER)
				.orElse(false);
	}
}
