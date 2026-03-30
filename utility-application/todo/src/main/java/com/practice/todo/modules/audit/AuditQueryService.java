package com.practice.todo.modules.audit;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditQueryService {

	private final ActivityLogRepository activityLogRepository;

	@Transactional(readOnly = true)
	public Page<ActivityLog> forEntity(String type, UUID id, int page, int size) {
		int p = Math.max(0, page);
		int s = Math.min(100, Math.max(1, size));
		return activityLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
				type, id, PageRequest.of(p, s));
	}

	@Transactional(readOnly = true)
	public Page<ActivityLog> forUser(UUID userId, int page, int size) {
		int p = Math.max(0, page);
		int s = Math.min(100, Math.max(1, size));
		return activityLogRepository.findByActor_IdOrderByCreatedAtDesc(userId, PageRequest.of(p, s));
	}

	public static ActivityLogResponse toResponse(ActivityLog log) {
		UUID actorId = log.getActor() != null ? log.getActor().getId() : null;
		return new ActivityLogResponse(
				log.getId(),
				log.getEntityType(),
				log.getEntityId(),
				actorId,
				log.getAction(),
				log.getMetadata(),
				null,
				log.getCreatedAt());
	}

	public static AuditPageResponse<ActivityLogResponse> toPage(Page<ActivityLog> p) {
		List<ActivityLogResponse> items = p.getContent().stream().map(AuditQueryService::toResponse).toList();
		return new AuditPageResponse<>(items, p.getNumber(), p.getSize(), p.getTotalElements());
	}
}
