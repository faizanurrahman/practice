package com.practice.todo.project.dto;

import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.Data;

@Data
public class ProjectPatchRequest {

	@Size(max = 200)
	private String name;

	@Size(max = 10_000)
	private String description;

	/** Omit field to leave unchanged; explicit null clears timeline */
	private Instant timelineEndAt;

	private Boolean clearTimelineEndAt;
}
