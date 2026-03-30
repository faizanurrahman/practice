package com.practice.todo.modules.workspace.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WorkspaceCreateRequest {

	@NotBlank
	@Size(max = 200)
	private String name;
}
