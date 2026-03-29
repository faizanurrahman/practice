package com.practice.todo.project.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProjectResponse {

	UUID id;
	String name;
	String description;
	Instant timelineEndAt;
	Instant createdAt;
	Instant updatedAt;
}
