package com.practice.todo.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.Data;

@Data
public class ProjectRequest {

	@NotBlank
	@Size(max = 200)
	private String name;

	@Size(max = 10_000)
	private String description;

	private Instant timelineEndAt;
}
