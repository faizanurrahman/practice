package com.practice.todo.modules.storage.application.model;

public record UploadResult(String objectKey, long sizeBytes, String contentType) {}
