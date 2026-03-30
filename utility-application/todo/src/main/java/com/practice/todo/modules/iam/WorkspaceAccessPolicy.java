package com.practice.todo.modules.iam;

import java.util.UUID;

public interface WorkspaceAccessPolicy {

	boolean isMember(UUID userId, UUID workspaceId);

	boolean isAdmin(UUID userId, UUID workspaceId);

	boolean isOwner(UUID userId, UUID workspaceId);
}
