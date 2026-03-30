package com.practice.todo.modules.analytics;

import com.practice.todo.modules.eventing.ProjectionEvent;
import com.practice.todo.modules.eventing.ProjectionEventRepository;
import com.practice.todo.modules.project.domain.event.ProjectCreatedEvent;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AnalyticsProjectionListener {

	private final AnalyticsCounterRepository analyticsCounterRepository;
	private final ProjectionEventRepository projectionEventRepository;

	@EventListener
	@Transactional
	public void onProjectCreated(ProjectCreatedEvent e) {
		if (projectionEventRepository.existsByProjectionAndEventId("analytics.project.created", e.eventId())) {
			return;
		}
		Optional<AnalyticsCounter> existing = analyticsCounterRepository.findByDimensionAndDimensionIdAndMetric(
				"WORKSPACE", e.workspaceId(), "projects_created");
		AnalyticsCounter c;
		if (existing.isPresent()) {
			c = existing.get();
			c.setCountValue(c.getCountValue() + 1);
		} else {
			c = new AnalyticsCounter();
			c.setDimension("WORKSPACE");
			c.setDimensionId(e.workspaceId());
			c.setMetric("projects_created");
			c.setCountValue(1);
		}
		analyticsCounterRepository.save(c);
		markProcessed("analytics.project.created", e.eventId());
	}

	private void markProcessed(String projection, java.util.UUID eventId) {
		ProjectionEvent event = new ProjectionEvent();
		event.setProjection(projection);
		event.setEventId(eventId);
		projectionEventRepository.save(event);
	}
}
