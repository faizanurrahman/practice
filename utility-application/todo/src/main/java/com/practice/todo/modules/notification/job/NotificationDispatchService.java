package com.practice.todo.modules.notification.job;

import com.practice.todo.modules.notification.channel.NotificationChannel;
import com.practice.todo.modules.notification.channel.NotificationMessage;
import com.practice.todo.modules.notification.channel.NotificationSenderFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationDispatchService {

	private final NotificationSenderFactory senderFactory;

	public void dispatch(NotificationDispatchPayload payload) {
		List<NotificationChannel> channels = payload.channels();
		if (channels == null || channels.isEmpty()) {
			channels = List.of(NotificationChannel.IN_APP);
		}
		NotificationMessage message = new NotificationMessage(
				payload.userId(),
				payload.type(),
				payload.title(),
				payload.body(),
				payload.dedupeKey());
		for (NotificationChannel channel : channels) {
			senderFactory.get(channel).send(message);
		}
	}
}
