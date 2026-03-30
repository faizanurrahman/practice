package com.practice.todo.modules.audit;

import com.practice.todo.modules.eventing.ProjectionEvent;
import com.practice.todo.modules.eventing.ProjectionEventRepository;
import com.practice.todo.modules.project.domain.event.ProjectCreatedEvent;
import com.practice.todo.modules.user.application.port.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ActivityLogListener {

	private final ActivityLogRepository activityLogRepository;
	private final UserRepositoryPort userRepository;
	private final ProjectionEventRepository projectionEventRepository;

	@EventListener
	@Transactional
	public void onProjectCreated(ProjectCreatedEvent e) {
		if (projectionEventRepository.existsByProjectionAndEventId("audit.project.created", e.eventId())) {
			return;
		}
		ActivityLog log = new ActivityLog();
		if (e.actorUserId() != null) {
			userRepository.findById(e.actorUserId()).ifPresent(log::setActor);
		}
		log.setAction("CREATED");
		log.setEntityType("PROJECT");
		log.setEntityId(e.projectId());
		log.setMetadata("{\"name\":\"" + escape(e.name()) + "\"}");
		activityLogRepository.save(log);
		markProcessed("audit.project.created", e.eventId());
	}

	private void markProcessed(String projection, java.util.UUID eventId) {
		ProjectionEvent event = new ProjectionEvent();
		event.setProjection(projection);
		event.setEventId(eventId);
		projectionEventRepository.save(event);
	}

	private static String escape(String s) {
		return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
	}
}
