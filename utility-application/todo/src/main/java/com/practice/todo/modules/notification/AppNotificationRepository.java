package com.practice.todo.modules.notification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppNotificationRepository extends JpaRepository<AppNotification, UUID> {

	List<AppNotification> findByUserIdOrderByCreatedAtDesc(UUID userId);

	Optional<AppNotification> findByIdAndUserId(UUID id, UUID userId);

	boolean existsByUserIdAndDedupeKey(UUID userId, String dedupeKey);
}
