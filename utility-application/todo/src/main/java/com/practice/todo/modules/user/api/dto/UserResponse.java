package com.practice.todo.modules.user.api.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserResponse {

	UUID id;
	String email;
	String displayName;
	Instant onboardingCompletedAt;
}
