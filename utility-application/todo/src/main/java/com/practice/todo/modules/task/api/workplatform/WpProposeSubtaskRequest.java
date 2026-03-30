package com.practice.todo.modules.task.api.workplatform;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WpProposeSubtaskRequest(
		@NotBlank @Size(max = 500) String title, String description) {}
