package com.practice.todo.modules.presence.domain;

import java.time.Instant;
import java.util.UUID;

public record TaskPresence(UUID taskId, UUID userId, String displayName, Instant lastSeenAt) {}
