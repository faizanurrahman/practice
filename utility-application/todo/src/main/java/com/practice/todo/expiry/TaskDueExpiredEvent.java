package com.practice.todo.expiry;

import java.time.Instant;
import java.util.UUID;

public record TaskDueExpiredEvent(UUID userId, UUID taskId, String taskTitle, Instant dueAt, String dedupeKey) {}
