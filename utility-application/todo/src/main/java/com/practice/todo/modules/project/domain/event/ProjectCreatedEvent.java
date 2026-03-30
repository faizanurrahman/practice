package com.practice.todo.modules.project.domain.event;

import java.util.UUID;

public record ProjectCreatedEvent(UUID eventId, UUID projectId, UUID workspaceId, String name, UUID actorUserId) {}
