package com.practice.todo.modules.audit;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {

	List<ActivityLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, UUID entityId);

	Page<ActivityLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
			String entityType, UUID entityId, Pageable pageable);

	Page<ActivityLog> findByActor_IdOrderByCreatedAtDesc(UUID actorUserId, Pageable pageable);
}
