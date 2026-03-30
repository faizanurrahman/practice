package com.practice.todo.modules.workspace.application.port;

import com.practice.todo.modules.workspace.domain.Workspace;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceRepositoryPort {

	Optional<Workspace> findById(UUID workspaceId);

	Workspace save(Workspace workspace);
}
