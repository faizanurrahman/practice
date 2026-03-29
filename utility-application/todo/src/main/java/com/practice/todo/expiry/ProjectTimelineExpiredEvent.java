package com.practice.todo.expiry;

import java.time.Instant;
import java.util.UUID;

public record ProjectTimelineExpiredEvent(
		UUID userId, UUID projectId, String projectName, Instant timelineEndAt, String dedupeKey) {}
