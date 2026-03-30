package com.practice.todo.modules.storage.application.model;

public record UploadCommand(byte[] data, String contentType, String originalFilename) {}
