package com.practice.todo.modules.task.api.workplatform;

import java.time.Instant;
import java.util.UUID;

public record WpTaskMemberResponse(UUID userId, String role, Instant joinedAt) {}
