package com.practice.todo.modules.notification.dto;

import com.practice.todo.modules.notification.NotificationType;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NotificationResponse {

	UUID id;
	NotificationType type;
	String title;
	String body;
	Instant readAt;
	Instant createdAt;
}
