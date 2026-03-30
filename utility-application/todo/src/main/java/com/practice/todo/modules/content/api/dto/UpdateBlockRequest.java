package com.practice.todo.modules.content.api.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateBlockRequest(@NotBlank String content) {}
