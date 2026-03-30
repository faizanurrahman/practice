package com.practice.todo.modules.project.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import com.practice.todo.modules.project.domain.ProjectVisibility;

@Data
public class ProjectRequest {

	@NotBlank
	@Size(max = 200)
	private String name;

	@Size(max = 10_000)
	private String description;

	private Instant timelineEndAt;

	private ProjectVisibility visibility;

	/** When null, the user's default (first) workspace is used. */
	private UUID workspaceId;
}
