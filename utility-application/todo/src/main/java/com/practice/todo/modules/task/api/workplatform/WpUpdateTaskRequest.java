package com.practice.todo.modules.task.api.workplatform;

import java.time.Instant;

public record WpUpdateTaskRequest(String title, String priority, Instant dueAt, Instant startAt) {}
