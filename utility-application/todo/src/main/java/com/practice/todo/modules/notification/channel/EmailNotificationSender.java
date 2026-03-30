package com.practice.todo.modules.notification.channel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailNotificationSender implements NotificationSender {

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.EMAIL;
	}

	@Override
	public void send(NotificationMessage message) {
		log.info("Email notification stub userId={} type={} title={}",
				message.userId(), message.type(), message.title());
	}
}
