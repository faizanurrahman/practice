package com.practice.todo.modules.workspace.api.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WorkspaceResponse {

	UUID id;
	String name;
	UUID createdByUserId;
	Instant createdAt;
	Instant updatedAt;
}
