package com.practice.todo.modules.notification.channel;

public interface NotificationSender {

	NotificationChannel channel();

	void send(NotificationMessage message);
}
