package com.practice.todo.modules.workspace.application.port;

import com.practice.todo.modules.workspace.domain.Workspace;
import java.util.List;
import java.util.UUID;

public interface WorkspaceLookupPort {

	Workspace getWorkspace(UUID workspaceId);

	UUID resolveDefaultWorkspaceId(UUID userId);

	List<UUID> listWorkspaceIdsForUser(UUID userId);
}
