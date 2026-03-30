package com.practice.todo.modules.task.api.workplatform;

import jakarta.validation.constraints.NotNull;

public record WpDecisionRequest(@NotNull Boolean approved) {}
