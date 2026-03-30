package com.practice.todo.modules.project.api.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import com.practice.todo.modules.project.domain.ProjectVisibility;

@Value
@Builder
public class ProjectResponse {

	UUID id;
	UUID workspaceId;
	String name;
	String description;
	Instant timelineEndAt;
	ProjectVisibility visibility;
	Instant archivedAt;
	Instant createdAt;
	Instant updatedAt;
}
