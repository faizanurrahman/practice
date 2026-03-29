package com.practice.todo.notification;

import com.practice.todo.notification.dto.NotificationResponse;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final AppNotificationRepository notificationRepository;

	@Transactional(readOnly = true)
	public List<NotificationResponse> list(UUID userId) {
		return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
				.map(NotificationService::toResponse)
				.collect(Collectors.toList());
	}

	@Transactional
	public void markRead(UUID userId, UUID notificationId) {
		AppNotification n = notificationRepository
				.findByIdAndUserId(notificationId, userId)
				.orElseThrow(() -> new IllegalArgumentException("Notification not found"));
		if (n.getReadAt() == null) {
			n.setReadAt(java.time.Instant.now());
			notificationRepository.save(n);
		}
	}

	private static NotificationResponse toResponse(AppNotification n) {
		return NotificationResponse.builder()
				.id(n.getId())
				.type(n.getType())
				.title(n.getTitle())
				.body(n.getBody())
				.readAt(n.getReadAt())
				.createdAt(n.getCreatedAt())
				.build();
	}
}
