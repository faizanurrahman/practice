package com.practice.todo.modules.notification.channel;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class NotificationSenderFactory {

	private final Map<NotificationChannel, NotificationSender> senders = new EnumMap<>(NotificationChannel.class);

	public NotificationSenderFactory(List<NotificationSender> senders) {
		for (NotificationSender sender : senders) {
			this.senders.put(sender.channel(), sender);
		}
	}

	public NotificationSender get(NotificationChannel channel) {
		NotificationSender sender = senders.get(channel);
		if (sender == null) {
			throw new IllegalStateException("No sender registered for " + channel);
		}
		return sender;
	}
}
