package com.practice.todo.modules.task.api.workplatform;

import jakarta.validation.constraints.NotBlank;

public record WpAddLabelRequest(@NotBlank String name, String color) {}
