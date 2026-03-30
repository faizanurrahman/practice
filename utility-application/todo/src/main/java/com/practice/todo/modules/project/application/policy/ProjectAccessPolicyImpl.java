package com.practice.todo.modules.project.application.policy;

import com.practice.todo.modules.iam.ProjectAccessPolicy;
import com.practice.todo.modules.iam.WorkspaceAccessPolicy;
import com.practice.todo.modules.project.application.port.ProjectRepositoryPort;
import com.practice.todo.modules.workspace.application.port.WorkspaceLookupPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectAccessPolicyImpl implements ProjectAccessPolicy {

	private final ProjectRepositoryPort projectRepository;
	private final WorkspaceLookupPort workspaceLookupPort;
	private final WorkspaceAccessPolicy workspaceAccessPolicy;

	@Override
	public boolean canAccessProject(UUID userId, UUID projectId) {
		return projectRepository
				.findByIdAndWorkspaceIdIn(projectId, workspaceLookupPort.listWorkspaceIdsForUser(userId))
				.isPresent();
	}

	@Override
	public boolean canModifyProject(UUID userId, UUID projectId) {
		return projectRepository.findById(projectId)
				.map(p -> workspaceAccessPolicy.isOwner(userId, p.getWorkspace().getId())
						|| workspaceAccessPolicy.isAdmin(userId, p.getWorkspace().getId()))
				.orElse(false);
	}
}
