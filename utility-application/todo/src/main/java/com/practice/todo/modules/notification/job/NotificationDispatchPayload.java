package com.practice.todo.modules.notification.job;

import com.practice.todo.modules.notification.NotificationType;
import com.practice.todo.modules.notification.channel.NotificationChannel;
import java.util.List;
import java.util.UUID;

public record NotificationDispatchPayload(
		UUID userId,
		NotificationType type,
		String title,
		String body,
		String dedupeKey,
		List<NotificationChannel> channels
) {}
