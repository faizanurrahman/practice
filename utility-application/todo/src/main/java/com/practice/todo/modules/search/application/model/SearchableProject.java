package com.practice.todo.modules.search.application.model;

import java.time.Instant;
import java.util.UUID;

public record SearchableProject(
		UUID id, UUID workspaceId, String name, String description, String visibility, Instant updatedAt) {}
