package com.practice.todo.modules.comments.api.dto;

import jakarta.validation.constraints.NotBlank;

public record EditCommentRequest(@NotBlank String body) {}
