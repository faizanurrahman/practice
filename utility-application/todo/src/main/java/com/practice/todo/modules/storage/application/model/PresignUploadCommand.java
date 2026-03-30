package com.practice.todo.modules.storage.application.model;

public record PresignUploadCommand(String contentType, long contentLength, String originalFilename) {}
