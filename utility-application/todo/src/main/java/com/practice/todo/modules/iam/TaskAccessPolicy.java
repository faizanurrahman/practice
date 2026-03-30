package com.practice.todo.modules.iam;

import java.util.UUID;

public interface TaskAccessPolicy {

	boolean canViewTask(UUID userId, UUID workspaceId);

	boolean canModifyTask(UUID userId, UUID workspaceId, UUID assigneeUserId);
}
