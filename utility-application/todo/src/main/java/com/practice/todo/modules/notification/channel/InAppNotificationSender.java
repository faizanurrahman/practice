package com.practice.todo.modules.notification.channel;

import com.practice.todo.modules.notification.AppNotification;
import com.practice.todo.modules.notification.AppNotificationRepository;
import com.practice.todo.modules.user.application.port.UserRepositoryPort;
import com.practice.todo.modules.user.domain.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InAppNotificationSender implements NotificationSender {

	private final AppNotificationRepository notificationRepository;
	private final UserRepositoryPort userRepository;

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.IN_APP;
	}

	@Override
	@Transactional
	public void send(NotificationMessage message) {
		UUID userId = message.userId();
		if (notificationRepository.existsByUserIdAndDedupeKey(userId, message.dedupeKey())) {
			return;
		}
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
		AppNotification n = new AppNotification();
		n.setUser(user);
		n.setType(message.type());
		n.setTitle(message.title());
		n.setBody(message.body());
		n.setDedupeKey(message.dedupeKey());
		notificationRepository.save(n);
	}
}
