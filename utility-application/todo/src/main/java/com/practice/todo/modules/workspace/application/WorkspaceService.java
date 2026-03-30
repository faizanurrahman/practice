package com.practice.todo.modules.workspace.application;

import com.practice.todo.modules.user.application.port.UserLookupPort;
import com.practice.todo.modules.user.domain.User;
import com.practice.todo.modules.workspace.api.dto.WorkspaceCreateRequest;
import com.practice.todo.modules.workspace.api.dto.WorkspaceResponse;
import com.practice.todo.modules.workspace.application.port.WorkspaceLookupPort;
import com.practice.todo.modules.workspace.application.port.WorkspaceMemberRepositoryPort;
import com.practice.todo.modules.workspace.application.port.WorkspaceRepositoryPort;
import com.practice.todo.modules.workspace.domain.Workspace;
import com.practice.todo.modules.workspace.domain.WorkspaceMember;
import com.practice.todo.modules.workspace.domain.WorkspaceRole;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkspaceService implements WorkspaceLookupPort {

	private final WorkspaceRepositoryPort workspaceRepository;
	private final WorkspaceMemberRepositoryPort workspaceMemberRepository;
	private final UserLookupPort userLookupPort;

	@Transactional(readOnly = true)
	public List<WorkspaceResponse> listForUser(UUID userId) {
		return workspaceMemberRepository.findByUserIdOrderByJoinedAtAsc(userId).stream()
				.map(m -> workspaceRepository.findById(m.getWorkspaceId()).orElseThrow())
				.map(WorkspaceService::toResponse)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public WorkspaceResponse get(UUID userId, UUID workspaceId) {
		ensureMember(userId, workspaceId);
		Workspace w = workspaceRepository
				.findById(workspaceId)
				.orElseThrow(() -> new IllegalArgumentException("Workspace not found"));
		return toResponse(w);
	}

	@Transactional
	public WorkspaceResponse create(UUID userId, WorkspaceCreateRequest req) {
		User creator = userLookupPort.getById(userId);
		Workspace w = new Workspace();
		w.setName(req.getName().trim());
		w.setCreatedBy(creator);
		workspaceRepository.save(w);
		WorkspaceMember m = new WorkspaceMember();
		m.setWorkspaceId(w.getId());
		m.setUserId(userId);
		m.setRole(WorkspaceRole.OWNER);
		m.setJoinedAt(Instant.now());
		workspaceMemberRepository.save(m);
		return toResponse(w);
	}

	@Override
	@Transactional(readOnly = true)
	public UUID resolveDefaultWorkspaceId(UUID userId) {
		return workspaceMemberRepository
				.findByUserIdOrderByJoinedAtAsc(userId)
				.stream()
				.findFirst()
				.map(WorkspaceMember::getWorkspaceId)
				.orElseThrow(() -> new IllegalStateException("User has no workspace"));
	}

	@Override
	@Transactional(readOnly = true)
	public List<UUID> listWorkspaceIdsForUser(UUID userId) {
		return workspaceMemberRepository.findByUserIdOrderByJoinedAtAsc(userId).stream()
				.map(WorkspaceMember::getWorkspaceId)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Workspace getWorkspace(UUID workspaceId) {
		return workspaceRepository
				.findById(workspaceId)
				.orElseThrow(() -> new IllegalArgumentException("Workspace not found"));
	}

	void ensureMember(UUID userId, UUID workspaceId) {
		if (!workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId)) {
			throw new IllegalArgumentException("Workspace not found");
		}
	}

	private static WorkspaceResponse toResponse(Workspace w) {
		return WorkspaceResponse.builder()
				.id(w.getId())
				.name(w.getName())
				.createdByUserId(w.getCreatedBy().getId())
				.createdAt(w.getCreatedAt())
				.updatedAt(w.getUpdatedAt())
				.build();
	}
}
