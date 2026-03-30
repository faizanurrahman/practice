package com.practice.todo.modules.storage.application.model;

import java.time.Instant;
import java.util.Map;

public record PresignedUpload(String objectKey, String uploadUrl, Map<String, String> headers, Instant expiresAt) {}
