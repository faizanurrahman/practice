package com.practice.todo.modules.task.application;

import com.practice.todo.modules.iam.TaskAccessPolicy;
import com.practice.todo.modules.project.application.port.ProjectAccessPort;
import com.practice.todo.modules.task.api.dto.TaskCreateRequest;
import com.practice.todo.modules.task.api.dto.TaskMoveRequest;
import com.practice.todo.modules.task.api.dto.TaskResponse;
import com.practice.todo.modules.task.api.workplatform.WpCreateTaskRequest;
import com.practice.todo.modules.task.api.workplatform.WpMoveTaskRequest;
import com.practice.todo.modules.task.api.workplatform.WpTaskMemberResponse;
import com.practice.todo.modules.task.api.workplatform.WpTaskResponse;
import com.practice.todo.modules.task.api.workplatform.WpUpdateTaskRequest;
import com.practice.todo.modules.task.application.port.LabelRepositoryPort;
import com.practice.todo.modules.task.application.port.TaskLabelRepositoryPort;
import com.practice.todo.modules.task.application.port.TaskRepositoryPort;
import com.practice.todo.modules.task.domain.JoinRequestStatus;
import com.practice.todo.modules.task.domain.Label;
import com.practice.todo.modules.task.domain.ProposalStatus;
import com.practice.todo.modules.task.domain.SubtaskProposal;
import com.practice.todo.modules.task.domain.Task;
import com.practice.todo.modules.task.domain.TaskHierarchyService;
import com.practice.todo.modules.task.domain.TaskJoinRequest;
import com.practice.todo.modules.task.domain.TaskLabel;
import com.practice.todo.modules.task.domain.TaskMember;
import com.practice.todo.modules.task.domain.TaskMemberRole;
import com.practice.todo.modules.task.domain.TaskPathCodec;
import com.practice.todo.modules.task.domain.TaskPriorityParser;
import com.practice.todo.modules.task.domain.TaskStatus;
import com.practice.todo.modules.task.application.port.SubtaskProposalRepositoryPort;
import com.practice.todo.modules.task.application.port.TaskJoinRequestRepositoryPort;
import com.practice.todo.modules.task.application.port.TaskMemberRepositoryPort;
import com.practice.todo.modules.user.application.port.UserRepositoryPort;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkPlatformTaskService {

	private final TaskService taskService;
	private final TaskRepositoryPort taskRepository;
	private final TaskMemberRepositoryPort taskMembers;
	private final TaskJoinRequestRepositoryPort joinRequests;
	private final SubtaskProposalRepositoryPort proposals;
	private final TaskMembershipService taskMembershipService;
	private final TaskLabelRepositoryPort taskLabels;
	private final LabelRepositoryPort labels;
	private final ProjectAccessPort projectAccessPort;
	private final TaskAccessPolicy taskAccessPolicy;
	private final UserRepositoryPort userRepository;
	private final TaskHierarchyService hierarchyService = new TaskHierarchyService();

	@Transactional
	public WpTaskResponse create(UUID userId, WpCreateTaskRequest body) {
		TaskCreateRequest r = new TaskCreateRequest();
		r.setTitle(body.title());
		r.setContent(body.description());
		r.setPriority(body.priority());
		r.setDueAt(body.dueAt());
		r.setStartAt(body.startAt());
		TaskResponse created = taskService.create(userId, body.projectId(), r);
		return toWp(loadActive(userId, created.getId()));
	}

	@Transactional(readOnly = true)
	public List<WpTaskResponse> listByProject(UUID userId, UUID projectId) {
		projectAccessPort.getAccessibleProject(userId, projectId);
		return taskRepository.findAllActiveByProjectId(projectId).stream()
				.filter(t -> taskAccessPolicy.canViewTask(
						userId, t.getProject().getWorkspace().getId()))
				.map(this::toWp)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<WpTaskResponse> listMyAssigned(UUID userId) {
		return taskRepository.findMyAssignedActive(userId).stream()
				.filter(t -> taskAccessPolicy.canViewTask(
						userId, t.getProject().getWorkspace().getId()))
				.map(this::toWp)
				.toList();
	}

	@Transactional(readOnly = true)
	public WpTaskResponse get(UUID userId, UUID taskId) {
		return toWp(loadActive(userId, taskId));
	}

	@Transactional(readOnly = true)
	public List<WpTaskResponse> tree(UUID userId, UUID taskId) {
		Task root = loadActive(userId, taskId);
		return taskRepository
				.findSubtreeByPathPrefix(root.getProject().getId(), root.getPath())
				.stream()
				.map(this::toWp)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<WpTaskMemberResponse> listMembers(UUID userId, UUID taskId) {
		loadActive(userId, taskId);
		return mapMembers(taskId);
	}

	@Transactional(readOnly = true)
	public List<TaskJoinRequest> pendingJoinRequests(UUID userId, UUID taskId) {
		Task t = loadActive(userId, taskId);
		requireOwner(userId, t.getId());
		return joinRequests.findByTaskIdAndStatus(taskId, JoinRequestStatus.PENDING);
	}

	@Transactional(readOnly = true)
	public List<SubtaskProposal> pendingSubtaskProposals(UUID userId, UUID taskId) {
		Task t = loadActive(userId, taskId);
		requireOwner(userId, t.getId());
		return proposals.findByParentTaskIdAndStatus(taskId, ProposalStatus.PROPOSED);
	}

	@Transactional
	public WpTaskResponse update(UUID userId, UUID taskId, WpUpdateTaskRequest body) {
		Task t = loadActive(userId, taskId);
		requireMember(userId, taskId);
		if (body.title() != null && !body.title().isBlank()) {
			t.setTitle(body.title().trim());
		}
		if (body.priority() != null && !body.priority().isBlank()) {
			t.setPriority(TaskPriorityParser.parseToStoredName(body.priority()));
		}
		if (body.dueAt() != null) {
			t.setDueAt(body.dueAt());
		}
		if (body.startAt() != null) {
			t.setStartAt(body.startAt());
		}
		taskRepository.save(t);
		return toWp(loadActive(userId, taskId));
	}

	@Transactional
	public WpTaskResponse move(UUID userId, UUID taskId, WpMoveTaskRequest body) {
		TaskMoveRequest m = new TaskMoveRequest();
		m.setNewParentTaskId(body.newParentId());
		TaskResponse moved = taskService.move(userId, taskId, m);
		return toWp(loadActive(userId, moved.getId()));
	}

	@Transactional
	public WpTaskResponse complete(UUID userId, UUID taskId) {
		TaskResponse done = taskService.complete(userId, taskId);
		return toWp(loadActive(userId, done.getId()));
	}

	@Transactional
	public void delete(UUID userId, UUID taskId) {
		taskService.delete(userId, taskId);
	}

	@Transactional
	public WpTaskResponse restore(UUID userId, UUID taskId) {
		Task t = taskRepository
				.findByIdWithProjectIncludingDeleted(taskId)
				.orElseThrow(() -> new IllegalArgumentException("Task not found"));
		UUID ws = t.getProject().getWorkspace().getId();
		if (!taskAccessPolicy.canViewTask(userId, ws)) {
			throw new IllegalArgumentException("Task not found");
		}
		if (!taskMembers.existsByTaskIdAndUserIdAndRole(taskId, userId, TaskMemberRole.OWNER)) {
			throw new IllegalArgumentException("Task not found");
		}
		if (t.getDeletedAt() == null) {
			throw new IllegalArgumentException("Task is not deleted.");
		}
		t.setDeletedAt(null);
		taskRepository.save(t);
		return toWp(loadActive(userId, taskId));
	}

	@Transactional
	public void addLabel(UUID userId, UUID taskId, String name, String color) {
		Task t = loadActive(userId, taskId);
		requireMember(userId, taskId);
		String trimmed = name.trim();
		Label lb = labels
				.findByWorkspaceIdAndNameIgnoreCase(t.getProject().getWorkspace().getId(), trimmed)
				.orElseGet(() -> {
					Label created = new Label();
					created.setWorkspaceId(t.getProject().getWorkspace().getId());
					created.setName(trimmed);
					created.setColor(color);
					return labels.save(created);
				});
		if (color != null && !color.isBlank()) {
			lb.setColor(color);
			labels.save(lb);
		}
		boolean linked = taskLabels.findByTaskId(taskId).stream()
				.anyMatch(tl -> tl.getLabelId().equals(lb.getId()));
		if (!linked) {
			TaskLabel tl = new TaskLabel();
			tl.setTaskId(taskId);
			tl.setLabelId(lb.getId());
			taskLabels.save(tl);
		}
	}

	@Transactional
	public void removeLabel(UUID userId, UUID taskId, UUID labelId) {
		loadActive(userId, taskId);
		requireMember(userId, taskId);
		boolean onTask = taskLabels.findByTaskId(taskId).stream()
				.anyMatch(tl -> tl.getLabelId().equals(labelId));
		if (!onTask) {
			throw new IllegalArgumentException("Label not found on this task.");
		}
		taskLabels.deleteByTaskIdAndLabelId(taskId, labelId);
	}

	@Transactional
	public TaskJoinRequest requestJoin(UUID userId, UUID taskId, String message) {
		Task t = loadActive(userId, taskId);
		if (userRepository.findById(userId).isEmpty()) {
			throw new IllegalArgumentException("User does not exist.");
		}
		if (taskMembers.findByTaskIdAndUserId(taskId, userId).isPresent()) {
			throw new IllegalArgumentException("User is already a member of this task.");
		}
		if (joinRequests.existsByTask_IdAndRequesterIdAndStatus(
				taskId, userId, JoinRequestStatus.PENDING)) {
			throw new IllegalArgumentException("A pending join request already exists for this user.");
		}
		TaskJoinRequest r = new TaskJoinRequest();
		r.setTask(t);
		r.setRequesterId(userId);
		r.setMessage(message);
		return joinRequests.save(r);
	}

	@Transactional
	public TaskJoinRequest decideJoin(UUID userId, UUID requestId, boolean approved) {
		TaskJoinRequest r = joinRequests
				.findByIdWithTask(requestId)
				.orElseThrow(() -> new IllegalArgumentException("Join request not found."));
		Task t = r.getTask();
		requireOwner(userId, t.getId());
		if (r.getStatus() != JoinRequestStatus.PENDING) {
			throw new IllegalArgumentException("Join request is no longer pending.");
		}
		Instant now = Instant.now();
		r.setDecidedAt(now);
		r.setDecidedByUserId(userId);
		if (approved) {
			r.setStatus(JoinRequestStatus.APPROVED);
			taskMembershipService.ensureMemberForTask(t, r.getRequesterId());
		} else {
			r.setStatus(JoinRequestStatus.REJECTED);
		}
		return joinRequests.save(r);
	}

	@Transactional
	public SubtaskProposal proposeSubtask(UUID userId, UUID parentTaskId, String title, String description) {
		Task parent = loadActive(userId, parentTaskId);
		requireMember(userId, parentTaskId);
		SubtaskProposal p = new SubtaskProposal();
		p.setParentTask(parent);
		p.setProposerId(userId);
		p.setTitle(title.trim());
		p.setDescription(description);
		return proposals.save(p);
	}

	@Transactional
	public SubtaskProposal decideSubtaskProposal(UUID userId, UUID proposalId, boolean approved) {
		SubtaskProposal p = proposals
				.findByIdWithParent(proposalId)
				.orElseThrow(() -> new IllegalArgumentException("Proposal not found."));
		Task parent = p.getParentTask();
		requireOwner(userId, parent.getId());
		if (p.getStatus() != ProposalStatus.PROPOSED) {
			throw new IllegalArgumentException("Proposal is no longer open.");
		}
		Instant now = Instant.now();
		p.setDecidedAt(now);
		p.setDecidedByUserId(userId);
		if (approved) {
			p.setStatus(ProposalStatus.APPROVED);
			createChildFromProposal(parent, p, userId);
		} else {
			p.setStatus(ProposalStatus.REJECTED);
		}
		return proposals.save(p);
	}

	private void createChildFromProposal(Task parent, SubtaskProposal proposal, UUID deciderId) {
		int nextSort = taskRepository.findChildren(parent.getId()).stream()
						.mapToInt(Task::getSortOrder)
						.max()
						.orElse(-1)
				+ 1;
		Task child = new Task();
		child.setProject(parent.getProject());
		child.setParent(parent);
		child.setTitle(proposal.getTitle());
		child.setContent(proposal.getDescription());
		child.setStatus(TaskStatus.TODO);
		child.setPriority("MEDIUM");
		child.setSortOrder(nextSort);
		String path = hierarchyService.computePathForNewTask(parent, nextSort);
		child.setPath(path);
		child.setDepth(TaskPathCodec.depthFromPath(path));
		taskRepository.save(child);
		UUID ownerId = taskMembers
				.findFirstByTask_IdAndRoleOrderByJoinedAtAsc(parent.getId(), TaskMemberRole.OWNER)
				.map(TaskMember::getUserId)
				.orElse(deciderId);
		taskMembershipService.addOwnerIfAbsent(child, ownerId);
		taskMembershipService.ensureMemberForTask(child, proposal.getProposerId());
	}

	private Task loadActive(UUID userId, UUID taskId) {
		Task t = taskRepository
				.findByIdWithProject(taskId)
				.orElseThrow(() -> new IllegalArgumentException("Task not found"));
		if (t.getDeletedAt() != null) {
			throw new IllegalArgumentException("Task not found");
		}
		if (!taskAccessPolicy.canViewTask(userId, t.getProject().getWorkspace().getId())) {
			throw new IllegalArgumentException("Task not found");
		}
		return t;
	}

	private void requireMember(UUID userId, UUID taskId) {
		if (taskMembers.findByTaskIdAndUserId(taskId, userId).isEmpty()) {
			throw new IllegalArgumentException("Task not found");
		}
	}

	private void requireOwner(UUID userId, UUID taskId) {
		if (!taskMembers.existsByTaskIdAndUserIdAndRole(taskId, userId, TaskMemberRole.OWNER)) {
			throw new IllegalArgumentException("Task not found");
		}
	}

	private List<WpTaskMemberResponse> mapMembers(UUID taskId) {
		return taskMembers.findByTaskIdOrderByJoinedAtAsc(taskId).stream()
				.map(m -> new WpTaskMemberResponse(
						m.getUserId(), m.getRole().name(), m.getJoinedAt()))
				.toList();
	}

	private WpTaskResponse toWp(Task t) {
		return new WpTaskResponse(
				t.getId(),
				t.getProject().getId(),
				t.getParent() == null ? null : t.getParent().getId(),
				t.getTitle(),
				t.getStatus().name(),
				t.getPriority() != null ? t.getPriority() : "MEDIUM",
				t.getDueAt(),
				t.getSortOrder(),
				t.getPath(),
				t.getDepth(),
				mapMembers(t.getId()),
				t.getCreatedAt());
	}
}
