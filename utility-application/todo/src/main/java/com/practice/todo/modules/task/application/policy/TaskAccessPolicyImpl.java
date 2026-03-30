package com.practice.todo.modules.task.application.policy;

import com.practice.todo.modules.iam.TaskAccessPolicy;
import com.practice.todo.modules.iam.WorkspaceAccessPolicy;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskAccessPolicyImpl implements TaskAccessPolicy {

	private final WorkspaceAccessPolicy workspaceAccessPolicy;

	@Override
	public boolean canViewTask(UUID userId, UUID workspaceId) {
		return workspaceAccessPolicy.isMember(userId, workspaceId);
	}

	@Override
	public boolean canModifyTask(UUID userId, UUID workspaceId, UUID assigneeUserId) {
		if (workspaceAccessPolicy.isOwner(userId, workspaceId) || workspaceAccessPolicy.isAdmin(userId, workspaceId)) {
			return true;
		}
		return assigneeUserId != null && assigneeUserId.equals(userId);
	}
}
