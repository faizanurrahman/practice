package com.practice.todo.modules.jobs;

import java.time.Instant;

public record ExpiryScanPayload(Instant now) {}
