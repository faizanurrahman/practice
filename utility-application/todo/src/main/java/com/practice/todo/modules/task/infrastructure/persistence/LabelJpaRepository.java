package com.practice.todo.modules.task.infrastructure.persistence;

import com.practice.todo.modules.task.application.port.LabelRepositoryPort;
import com.practice.todo.modules.task.domain.Label;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelJpaRepository extends JpaRepository<Label, UUID>, LabelRepositoryPort {

	Optional<Label> findByWorkspaceIdAndNameIgnoreCase(UUID workspaceId, String name);

	List<Label> findByIdIn(Collection<UUID> ids);
}
