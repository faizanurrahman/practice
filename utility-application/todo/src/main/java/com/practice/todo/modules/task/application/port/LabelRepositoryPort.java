package com.practice.todo.modules.task.application.port;

import com.practice.todo.modules.task.domain.Label;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LabelRepositoryPort {

	Optional<Label> findByWorkspaceIdAndNameIgnoreCase(UUID workspaceId, String name);

	List<Label> findByIdIn(Collection<UUID> ids);

	Label save(Label label);
}
