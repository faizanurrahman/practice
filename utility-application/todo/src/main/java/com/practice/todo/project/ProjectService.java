package com.practice.todo.project;

import com.practice.todo.project.dto.ProjectPatchRequest;
import com.practice.todo.project.dto.ProjectRequest;
import com.practice.todo.project.dto.ProjectResponse;
import com.practice.todo.user.User;
import com.practice.todo.user.UserService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectRepository projectRepository;
	private final UserService userService;

	@Transactional(readOnly = true)
	public List<ProjectResponse> list(UUID userId) {
		return projectRepository.findByOwnerIdOrderByCreatedAtDesc(userId).stream()
				.map(ProjectService::toResponse)
				.collect(Collectors.toList());
	}

	@Transactional
	public ProjectResponse create(UUID userId, ProjectRequest req) {
		User owner = userService.getById(userId);
		Project p = new Project();
		p.setOwner(owner);
		p.setName(req.getName().trim());
		p.setDescription(req.getDescription());
		p.setTimelineEndAt(req.getTimelineEndAt());
		projectRepository.save(p);
		return toResponse(p);
	}

	@Transactional(readOnly = true)
	public ProjectResponse get(UUID userId, UUID projectId) {
		Project p = projectRepository
				.findByIdAndOwnerId(projectId, userId)
				.orElseThrow(() -> new IllegalArgumentException("Project not found"));
		return toResponse(p);
	}

	@Transactional
	public ProjectResponse patch(UUID userId, UUID projectId, ProjectPatchRequest req) {
		Project p = projectRepository
				.findByIdAndOwnerId(projectId, userId)
				.orElseThrow(() -> new IllegalArgumentException("Project not found"));
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
		return toResponse(projectRepository.save(p));
	}

	@Transactional
	public void delete(UUID userId, UUID projectId) {
		Project p = projectRepository
				.findByIdAndOwnerId(projectId, userId)
				.orElseThrow(() -> new IllegalArgumentException("Project not found"));
		projectRepository.delete(p);
	}

	@Transactional(readOnly = true)
	public Project getOwnedProject(UUID userId, UUID projectId) {
		return projectRepository
				.findByIdAndOwnerId(projectId, userId)
				.orElseThrow(() -> new IllegalArgumentException("Project not found"));
	}

	private static ProjectResponse toResponse(Project p) {
		return ProjectResponse.builder()
				.id(p.getId())
				.name(p.getName())
				.description(p.getDescription())
				.timelineEndAt(p.getTimelineEndAt())
				.createdAt(p.getCreatedAt())
				.updatedAt(p.getUpdatedAt())
				.build();
	}
}
