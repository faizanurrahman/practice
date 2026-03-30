package com.practice.todo.modules.project.api.dto;

import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.Data;
import com.practice.todo.modules.project.domain.ProjectVisibility;

@Data
public class ProjectPatchRequest {

	@Size(max = 200)
	private String name;

	@Size(max = 10_000)
	private String description;

	/** Omit field to leave unchanged; explicit null clears timeline */
	private Instant timelineEndAt;

	private Boolean clearTimelineEndAt;

	private ProjectVisibility visibility;

	/** True archives, false unarchives, null no change. */
	private Boolean archived;
}
