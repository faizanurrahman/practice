package com.practice.todo.modules.user.api.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PatchUserRequest {

	private Boolean onboardingComplete;

	@Size(max = 200)
	private String displayName;
}
