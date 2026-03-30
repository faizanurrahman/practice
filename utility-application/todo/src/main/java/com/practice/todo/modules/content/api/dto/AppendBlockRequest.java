package com.practice.todo.modules.content.api.dto;

import jakarta.validation.constraints.NotBlank;

public record AppendBlockRequest(@NotBlank String blockType, @NotBlank String content) {}
