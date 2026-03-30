package com.practice.todo.modules.notification.channel;

import com.practice.todo.modules.notification.NotificationType;
import java.util.UUID;

public record NotificationMessage(
		UUID userId,
		NotificationType type,
		String title,
		String body,
		String dedupeKey
) {}
