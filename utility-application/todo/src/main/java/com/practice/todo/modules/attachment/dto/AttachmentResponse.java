package com.practice.todo.modules.attachment.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AttachmentResponse {

	UUID id;
	UUID taskId;
	String originalFilename;
	String contentType;
	long sizeBytes;
	Instant createdAt;
}
