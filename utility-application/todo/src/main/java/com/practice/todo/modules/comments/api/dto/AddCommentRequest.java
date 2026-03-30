package com.practice.todo.modules.comments.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AddCommentRequest(
		@NotNull UUID blockId, UUID parentCommentId, @NotBlank String body) {}
