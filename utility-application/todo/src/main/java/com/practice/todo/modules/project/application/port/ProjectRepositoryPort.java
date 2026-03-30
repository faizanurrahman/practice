package com.practice.todo.modules.project.application.port;

import com.practice.todo.modules.project.domain.Project;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepositoryPort {

	List<Project> findByWorkspaceIdInAndArchivedAtIsNullOrderByCreatedAtDesc(Collection<UUID> workspaceIds);

	Optional<Project> findById(UUID id);

	Optional<Project> findByIdAndWorkspaceIdIn(UUID id, Collection<UUID> workspaceIds);

	List<Project> findWithPastTimeline(Instant now);

	Project save(Project project);

	void delete(Project project);
}
