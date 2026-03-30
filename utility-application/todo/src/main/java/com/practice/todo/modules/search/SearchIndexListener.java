package com.practice.todo.modules.search;

import com.practice.todo.modules.eventing.ProjectionEvent;
import com.practice.todo.modules.eventing.ProjectionEventRepository;
import com.practice.todo.modules.project.domain.event.ProjectCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SearchIndexListener {

	private final SearchDocumentRepository searchDocumentRepository;
	private final ProjectionEventRepository projectionEventRepository;

	@EventListener
	@Transactional
	public void onProjectCreated(ProjectCreatedEvent e) {
		if (projectionEventRepository.existsByProjectionAndEventId("search.project.created", e.eventId())) {
			return;
		}
		SearchDocument d = searchDocumentRepository
				.findByDocTypeAndRefId("PROJECT", e.projectId())
				.orElseGet(SearchDocument::new);
		d.setDocType("PROJECT");
		d.setRefId(e.projectId());
		d.setTitle(e.name());
		d.setWorkspaceId(e.workspaceId());
		searchDocumentRepository.save(d);
		markProcessed("search.project.created", e.eventId());
	}

	private void markProcessed(String projection, java.util.UUID eventId) {
		ProjectionEvent event = new ProjectionEvent();
		event.setProjection(projection);
		event.setEventId(eventId);
		projectionEventRepository.save(event);
	}
}
