package com.practice.todo.modules.task.api.workplatform;

import java.util.UUID;

public record WpMoveTaskRequest(UUID newParentId) {}
