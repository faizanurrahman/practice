package com.practice.todo.modules.project.application;

import com.practice.todo.modules.eventing.OutboxService;
import com.practice.todo.modules.iam.ProjectAccessPolicy;
import com.practice.todo.modules.iam.WorkspaceAccessPolicy;
import com.practice.todo.modules.project.api.dto.ProjectPatchRequest;
import com.practice.todo.modules.project.api.dto.ProjectRequest;
import com.practice.todo.modules.project.api.dto.ProjectResponse;
import com.practice.todo.modules.project.application.port.ProjectAccessPort;
import com.practice.todo.modules.project.application.port.ProjectRepositoryPort;
import com.practice.todo.modules.project.domain.Project;
import com.practice.todo.modules.project.domain.ProjectVisibility;
import com.practice.todo.modules.project.domain.event.ProjectCreatedEvent;
import com.practice.todo.modules.workspace.application.port.WorkspaceLookupPort;
import com.practice.todo.modules.workspace.domain.Workspace;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService implements ProjectAccessPort {

	private final ProjectRepositoryPort projectRepository;
	private final WorkspaceLookupPort workspaceLookupPort;
	private final WorkspaceAccessPolicy workspaceAccessPolicy;
	private final ProjectAccessPolicy projectAccessPolicy;
	private final OutboxService outboxService;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Transactional(readOnly = true)
	public List<ProjectResponse> list(UUID userId) {
		List<UUID> workspaceIds = workspaceLookupPort.listWorkspaceIdsForUser(userId);
		return projectRepository.findByWorkspaceIdInAndArchivedAtIsNullOrderByCreatedAtDesc(workspaceIds).stream()
				.map(ProjectService::toResponse)
				.collect(Collectors.toList());
	}

	@Transactional
	public ProjectResponse create(UUID userId, ProjectRequest req) {
		UUID workspaceId =
				req.getWorkspaceId() != null ? req.getWorkspaceId() : workspaceLookupPort.resolveDefaultWorkspaceId(userId);
		if (!workspaceAccessPolicy.isMember(userId, workspaceId)) {
			throw new IllegalArgumentException("Workspace not found");
		}
		Workspace ws = workspaceLookupPort.getWorkspace(workspaceId);
		Project p = new Project();
		p.setWorkspace(ws);
		p.setName(req.getName().trim());
		p.setDescription(req.getDescription());
		p.setTimelineEndAt(req.getTimelineEndAt());
		p.setVisibility(req.getVisibility() != null ? req.getVisibility() : ProjectVisibility.WORKSPACE);
		projectRepository.save(p);
		UUID eventId = UUID.randomUUID();
		enqueueProjectCreated(eventId, p);
		applicationEventPublisher.publishEvent(
				new ProjectCreatedEvent(eventId, p.getId(), p.getWorkspace().getId(), p.getName(), userId));
		return toResponse(p);
	}

	private void enqueueProjectCreated(UUID eventId, Project p) {
		Map<String, Object> payload = new LinkedHashMap<>();
		payload.put("eventId", eventId.toString());
		payload.put("projectId", p.getId().toString());
		payload.put("workspaceId", p.getWorkspace().getId().toString());
		payload.put("name", p.getName());
		payload.put("visibility", p.getVisibility().name());
		outboxService.enqueue("project-events", p.getId().toString(), "project.created", payload, "projects");
	}

	@Transactional(readOnly = true)
	public ProjectResponse get(UUID userId, UUID projectId) {
		Project p = loadAccessible(userId, projectId);
		return toResponse(p);
	}

	@Transactional
	public ProjectResponse patch(UUID userId, UUID projectId, ProjectPatchRequest req) {
		Project p = loadAccessible(userId, projectId);
		assertCanModify(userId, projectId);
		if (req.getName() != null && !req.getName().isBlank()) {
			p.setName(req.getName().trim());
		}
		if (req.getDescription() != null) {
			p.setDescription(req.getDescription());
		}
		if (Boolean.TRUE.equals(req.getClearTimelineEndAt())) {
			p.setTimelineEndAt(null);
		} else if (req.getTimelineEndAt() != null) {
			p.setTimelineEndAt(req.getTimelineEndAt());
		}
		if (req.getVisibility() != null) {
			p.setVisibility(req.getVisibility());
		}
		if (req.getArchived() != null) {
			p.setArchivedAt(req.getArchived() ? java.time.Instant.now() : null);
		}
		return toResponse(projectRepository.save(p));
	}

	@Transactional
	public void delete(UUID userId, UUID projectId) {
		Project p = loadAccessible(userId, projectId);
		assertCanModify(userId, projectId);
		projectRepository.delete(p);
	}

	@Override
	@Transactional(readOnly = true)
	public Project getAccessibleProject(UUID userId, UUID projectId) {
		return loadAccessible(userId, projectId);
	}

	private Project loadAccessible(UUID userId, UUID projectId) {
		if (!projectAccessPolicy.canAccessProject(userId, projectId)) {
			throw new IllegalArgumentException("Project not found");
		}
		return projectRepository
				.findById(projectId)
				.orElseThrow(() -> new IllegalArgumentException("Project not found"));
	}

	private void assertCanModify(UUID userId, UUID projectId) {
		if (!projectAccessPolicy.canModifyProject(userId, projectId)) {
			throw new IllegalArgumentException("Project not found");
		}
	}

	private static ProjectResponse toResponse(Project p) {
		return ProjectResponse.builder()
				.id(p.getId())
				.workspaceId(p.getWorkspace().getId())
				.name(p.getName())
				.description(p.getDescription())
				.timelineEndAt(p.getTimelineEndAt())
				.visibility(p.getVisibility())
				.archivedAt(p.getArchivedAt())
				.createdAt(p.getCreatedAt())
				.updatedAt(p.getUpdatedAt())
				.build();
	}
}
