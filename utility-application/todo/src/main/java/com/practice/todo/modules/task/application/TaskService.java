package com.practice.todo.modules.task.application;

import com.practice.todo.modules.eventing.OutboxService;
import com.practice.todo.modules.iam.TaskAccessPolicy;
import com.practice.todo.modules.iam.WorkspaceAccessPolicy;
import com.practice.todo.modules.project.application.port.ProjectAccessPort;
import com.practice.todo.modules.project.domain.Project;
import com.practice.todo.modules.task.application.port.LabelRepositoryPort;
import com.practice.todo.modules.task.application.port.TaskLabelRepositoryPort;
import com.practice.todo.modules.task.application.port.TaskRepositoryPort;
import com.practice.todo.modules.task.domain.Label;
import com.practice.todo.modules.task.domain.Task;
import com.practice.todo.modules.task.domain.TaskHierarchyService;
import com.practice.todo.modules.task.domain.TaskLabel;
import com.practice.todo.modules.task.domain.TaskPathCodec;
import com.practice.todo.modules.task.domain.TaskPriorityParser;
import com.practice.todo.modules.task.domain.TaskStatus;
import com.practice.todo.modules.task.api.dto.TaskCreateRequest;
import com.practice.todo.modules.task.api.dto.TaskMoveRequest;
import com.practice.todo.modules.task.api.dto.TaskPatchRequest;
import com.practice.todo.modules.task.api.dto.TaskReorderRequest;
import com.practice.todo.modules.task.api.dto.TaskResponse;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {

	private static final int MAX_LABELS = 20;
	private static final int MAX_LABEL_LENGTH = 200;

	private final TaskRepositoryPort taskRepository;
	private final LabelRepositoryPort labelRepository;
	private final TaskLabelRepositoryPort taskLabelRepository;
	private final ProjectAccessPort projectAccessPort;
	private final WorkspaceAccessPolicy workspaceAccessPolicy;
	private final TaskAccessPolicy taskAccessPolicy;
	private final OutboxService outboxService;
	private final TaskMembershipService taskMembershipService;
	private final TaskHierarchyService taskHierarchyService = new TaskHierarchyService();

	@Transactional(readOnly = true)
	public List<TaskResponse> list(UUID userId, UUID projectId, UUID parentTaskId) {
		projectAccessPort.getAccessibleProject(userId, projectId);
		if (parentTaskId == null) {
			return mapToResponses(taskRepository.findRootsByProjectId(projectId));
		}
		Task parent = taskRepository
				.findByIdAndDeletedAtIsNull(parentTaskId)
				.orElseThrow(() -> new IllegalArgumentException("Parent task not found"));
		if (!parent.getProject().getId().equals(projectId)) {
			throw new IllegalArgumentException("Parent task is not in this project");
		}
		return mapToResponses(taskRepository.findChildren(parentTaskId));
	}

	@Transactional
	public TaskResponse create(UUID userId, UUID projectId, TaskCreateRequest req) {
		Project p = projectAccessPort.getAccessibleProject(userId, projectId);
		Task task = new Task();
		task.setProject(p);
		if (req.getParentTaskId() != null) {
			Task parent = taskRepository
					.findByIdAndDeletedAtIsNull(req.getParentTaskId())
					.orElseThrow(() -> new IllegalArgumentException("Parent task not found"));
			if (!parent.getProject().getId().equals(projectId)) {
				throw new IllegalArgumentException("Parent task is not in this project");
			}
			task.setParent(parent);
		}
		task.setTitle(req.getTitle().trim());
		task.setContent(req.getContent());
		task.setDueAt(req.getDueAt());
		task.setStartAt(req.getStartAt());
		validateDateOrder(task.getStartAt(), task.getDueAt());
		if (req.getAssigneeUserId() != null) {
			ensureAssigneeMember(p.getWorkspace().getId(), req.getAssigneeUserId());
			task.setAssigneeUserId(req.getAssigneeUserId());
		}
		task.setStatus(req.getStatus() != null ? req.getStatus() : TaskStatus.TODO);
		task.setPriority(TaskPriorityParser.parseToStoredName(req.getPriority()));
		task.setSortOrder(nextSortOrder(projectId, req.getParentTaskId()));
		applyPathAndDepthForNew(task);
		taskRepository.save(task);
		taskMembershipService.onTaskCreated(task, userId);
		if (req.getLabels() != null) {
			replaceTaskLabels(task, req.getLabels());
		}
		enqueueTaskEvent("task.created", task, userId);
		return toResponse(task, labelsForTask(task.getId()));
	}

	@Transactional(readOnly = true)
	public TaskResponse get(UUID userId, UUID taskId) {
		Task t = loadForUser(userId, taskId);
		return toResponse(t, labelsForTask(t.getId()));
	}

	/** Resolves the task if the user may access it (e.g. for attachment linking). */
	@Transactional(readOnly = true)
	public Task loadTaskEntityForUser(UUID userId, UUID taskId) {
		return loadForUser(userId, taskId);
	}

	@Transactional(readOnly = true)
	public List<TaskResponse> subtree(UUID userId, UUID taskId, int maxDepth) {
		Task root = loadForUser(userId, taskId);
		int cap = Math.max(0, maxDepth);
		int baseDepth = root.getDepth();
		List<Task> subtree = taskRepository
				.findSubtreeByPathPrefix(root.getProject().getId(), root.getPath())
				.stream()
				.filter(t -> t.getDepth() - baseDepth <= cap)
				.toList();
		return mapToResponses(subtree);
	}

	@Transactional
	public TaskResponse patch(UUID userId, UUID taskId, TaskPatchRequest req) {
		Task t = loadForUser(userId, taskId);
		assertCanModify(userId, t);
		UUID previousAssignee = t.getAssigneeUserId();
		if (req.getTitle() != null) {
			t.setTitle(req.getTitle().trim());
		}
		if (req.getContent() != null) {
			t.setContent(req.getContent());
		}
		if (Boolean.TRUE.equals(req.getClearDueAt())) {
			t.setDueAt(null);
		} else if (req.getDueAt() != null) {
			t.setDueAt(req.getDueAt());
		}
		if (Boolean.TRUE.equals(req.getClearStartAt())) {
			t.setStartAt(null);
		} else if (req.getStartAt() != null) {
			t.setStartAt(req.getStartAt());
		}
		validateDateOrder(t.getStartAt(), t.getDueAt());
		if (Boolean.TRUE.equals(req.getClearAssignee())) {
			t.setAssigneeUserId(null);
		} else if (req.getAssigneeUserId() != null) {
			ensureAssigneeMember(t.getProject().getWorkspace().getId(), req.getAssigneeUserId());
			t.setAssigneeUserId(req.getAssigneeUserId());
		}
		if (req.getStatus() != null) {
			if (req.getStatus() == TaskStatus.DONE && anyIncompleteDescendant(taskId)) {
				throw new IllegalArgumentException("Cannot complete task while a descendant is incomplete");
			}
			t.setStatus(req.getStatus());
		}
		if (req.getPriority() != null) {
			t.setPriority(TaskPriorityParser.parseToStoredName(req.getPriority()));
		}
		taskMembershipService.syncAssignee(t, previousAssignee, t.getAssigneeUserId());
		taskRepository.save(t);
		if (Boolean.TRUE.equals(req.getClearLabels())) {
			taskLabelRepository.deleteByTaskId(taskId);
		} else if (req.getLabels() != null) {
			replaceTaskLabels(t, req.getLabels());
		}
		enqueueTaskEvent("task.updated", t, userId);
		return toResponse(t, labelsForTask(taskId));
	}

	@Transactional
	public TaskResponse complete(UUID userId, UUID taskId) {
		TaskPatchRequest p = new TaskPatchRequest();
		p.setStatus(TaskStatus.DONE);
		return patch(userId, taskId, p);
	}

	@Transactional
	public void delete(UUID userId, UUID taskId) {
		Task t = loadForUser(userId, taskId);
		assertCanModify(userId, t);
		List<UUID> ids = collectSubtreeIds(t.getId());
		taskRepository.markDeleted(ids, Instant.now());
	}

	@Transactional
	public TaskResponse move(UUID userId, UUID taskId, TaskMoveRequest req) {
		Task t = loadForUser(userId, taskId);
		assertCanModify(userId, t);
		UUID projectId = t.getProject().getId();
		UUID newParentId = req.getNewParentTaskId();
		String oldPath = t.getPath();
		if (newParentId != null) {
			if (newParentId.equals(taskId)) {
				throw new IllegalArgumentException("Cannot move task under itself");
			}
			Task parent = taskRepository
					.findByIdAndDeletedAtIsNull(newParentId)
					.orElseThrow(() -> new IllegalArgumentException("Parent task not found"));
			if (!parent.getProject().getId().equals(projectId)) {
				throw new IllegalArgumentException("Parent task is not in this project");
			}
			if (taskHierarchyService.isDescendantPath(oldPath, parent.getPath())) {
				throw new IllegalArgumentException("Cannot move task under its own descendant");
			}
			t.setParent(parent);
		} else {
			t.setParent(null);
		}
		int next = nextSortOrderExcluding(projectId, newParentId, taskId);
		t.setSortOrder(next);
		String newPathT = taskHierarchyService.computePathForNewTask(t.getParent(), t.getSortOrder());
		List<Task> subtree = taskRepository.findSubtreeByPathPrefix(projectId, oldPath);
		taskHierarchyService.rewriteSubtreePaths(t, newPathT, subtree);
		taskRepository.saveAllTree(subtree);
		enqueueTaskEvent("task.moved", t, userId);
		return toResponse(t, labelsForTask(t.getId()));
	}

	@Transactional
	public void reorder(UUID userId, UUID projectId, UUID parentTaskId, TaskReorderRequest req) {
		projectAccessPort.getAccessibleProject(userId, projectId);
		List<Task> siblings =
				parentTaskId == null
						? taskRepository.findRootsByProjectId(projectId)
						: taskRepository.findChildren(parentTaskId);
		Set<UUID> expected = siblings.stream().map(Task::getId).collect(Collectors.toSet());
		Set<UUID> ordered = new HashSet<>(req.getOrderedTaskIds());
		if (!expected.equals(ordered)) {
			throw new IllegalArgumentException("orderedTaskIds must list every sibling exactly once");
		}
		for (int i = 0; i < req.getOrderedTaskIds().size(); i++) {
			UUID id = req.getOrderedTaskIds().get(i);
			Task task = taskRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new IllegalArgumentException("Task not found"));
			if (!task.getProject().getId().equals(projectId)) {
				throw new IllegalArgumentException("Task not in project");
			}
			assertCanModify(userId, task);
			UUID pp = task.getParent() == null ? null : task.getParent().getId();
			if (!Objects.equals(pp, parentTaskId)) {
				throw new IllegalArgumentException("Task is not a sibling under the given parent");
			}
			task.setSortOrder(i);
		}
		rebuildPathsForProject(projectId);
	}

	private Task loadForUser(UUID userId, UUID taskId) {
		Task t = taskRepository
				.findByIdWithProject(taskId)
				.orElseThrow(() -> new IllegalArgumentException("Task not found"));
		UUID workspaceId = t.getProject().getWorkspace().getId();
		if (!taskAccessPolicy.canViewTask(userId, workspaceId)) {
			throw new IllegalArgumentException("Task not found");
		}
		return t;
	}

	private void assertCanModify(UUID userId, Task task) {
		UUID workspaceId = task.getProject().getWorkspace().getId();
		if (!taskAccessPolicy.canModifyTask(userId, workspaceId, task.getAssigneeUserId())) {
			throw new IllegalArgumentException("Task not found");
		}
	}

	private int nextSortOrder(UUID projectId, UUID parentTaskId) {
		return nextSortOrderExcluding(projectId, parentTaskId, null);
	}

	private int nextSortOrderExcluding(UUID projectId, UUID parentTaskId, UUID excludeTaskId) {
		List<Task> siblings =
				parentTaskId == null
						? taskRepository.findRootsByProjectId(projectId)
						: taskRepository.findChildren(parentTaskId);
		return siblings.stream()
				.filter(x -> excludeTaskId == null || !x.getId().equals(excludeTaskId))
				.mapToInt(Task::getSortOrder)
				.max()
				.orElse(-1)
				+ 1;
	}

	private boolean anyIncompleteDescendant(UUID taskId) {
		for (UUID cid : taskRepository.findChildIds(taskId)) {
			Task c = taskRepository.findByIdAndDeletedAtIsNull(cid).orElse(null);
			if (c == null) {
				continue;
			}
			if (c.getStatus() != TaskStatus.DONE) {
				return true;
			}
			if (anyIncompleteDescendant(cid)) {
				return true;
			}
		}
		return false;
	}

	private void applyPathAndDepthForNew(Task task) {
		String path = taskHierarchyService.computePathForNewTask(task.getParent(), task.getSortOrder());
		task.setPath(path);
		task.setDepth(TaskPathCodec.depthFromPath(path));
	}

	private void rebuildPathsForProject(UUID projectId) {
		for (Task r : taskRepository.findRootsByProjectId(projectId)) {
			rebuildPathRecursive(r, TaskPathCodec.segment(r.getSortOrder()), 0);
		}
	}

	private void rebuildPathRecursive(Task node, String path, int depth) {
		node.setPath(path);
		node.setDepth(depth);
		taskRepository.save(node);
		for (Task c : taskRepository.findChildren(node.getId())) {
			rebuildPathRecursive(c, path + "." + TaskPathCodec.segment(c.getSortOrder()), depth + 1);
		}
	}

	private List<UUID> collectSubtreeIds(UUID rootId) {
		List<UUID> all = new ArrayList<>();
		ArrayDeque<UUID> q = new ArrayDeque<>();
		q.add(rootId);
		while (!q.isEmpty()) {
			UUID id = q.removeFirst();
			all.add(id);
			for (UUID c : taskRepository.findChildIds(id)) {
				q.add(c);
			}
		}
		return all;
	}

	private Instant computeEffectiveDue(Task t) {
		Task cur = t;
		while (cur != null) {
			if (cur.getDueAt() != null) {
				return cur.getDueAt();
			}
			if (cur.getParent() == null) {
				break;
			}
			cur = taskRepository
					.findByIdAndDeletedAtIsNull(cur.getParent().getId())
					.orElse(null);
		}
		return t.getProject().getTimelineEndAt();
	}

	private void ensureAssigneeMember(UUID workspaceId, UUID assigneeUserId) {
		if (!workspaceAccessPolicy.isMember(assigneeUserId, workspaceId)) {
			throw new IllegalArgumentException("Assignee must be a workspace member");
		}
	}

	private List<TaskResponse> mapToResponses(List<Task> tasks) {
		Map<UUID, List<String>> labelsByTaskId = labelsByTaskIds(tasks);
		return tasks.stream()
				.map(task -> toResponse(task, labelsByTaskId.getOrDefault(task.getId(), List.of())))
				.collect(Collectors.toList());
	}

	private Map<UUID, List<String>> labelsByTaskIds(List<Task> tasks) {
		if (tasks.isEmpty()) {
			return Map.of();
		}
		List<UUID> taskIds = tasks.stream().map(Task::getId).toList();
		Map<UUID, List<String>> result = new LinkedHashMap<>();
		for (TaskLabel tl : taskLabelRepository.findByTaskIdIn(taskIds)) {
			result.computeIfAbsent(tl.getTaskId(), k -> new ArrayList<>()).add(tl.getLabel().getName());
		}
		return result;
	}

	private List<String> labelsForTask(UUID taskId) {
		return taskLabelRepository.findByTaskId(taskId).stream()
				.map(tl -> tl.getLabel().getName())
				.toList();
	}

	private void replaceTaskLabels(Task task, List<String> labels) {
		taskLabelRepository.deleteByTaskId(task.getId());
		List<String> normalized = normalizeLabels(labels);
		if (normalized.isEmpty()) {
			return;
		}
		UUID workspaceId = task.getProject().getWorkspace().getId();
		for (String name : normalized) {
			Label label = labelRepository.findByWorkspaceIdAndNameIgnoreCase(workspaceId, name)
					.orElseGet(() -> {
						Label created = new Label();
						created.setWorkspaceId(workspaceId);
						created.setName(name);
						return labelRepository.save(created);
					});
			TaskLabel taskLabel = new TaskLabel();
			taskLabel.setTaskId(task.getId());
			taskLabel.setLabelId(label.getId());
			taskLabelRepository.save(taskLabel);
		}
	}

	private List<String> normalizeLabels(List<String> labels) {
		if (labels == null || labels.isEmpty()) {
			return List.of();
		}
		Map<String, String> deduped = new LinkedHashMap<>();
		for (String raw : labels) {
			if (raw == null) {
				continue;
			}
			String trimmed = raw.trim();
			if (trimmed.isEmpty() || trimmed.length() > MAX_LABEL_LENGTH) {
				continue;
			}
			String key = trimmed.toLowerCase(Locale.ROOT);
			deduped.putIfAbsent(key, trimmed);
			if (deduped.size() >= MAX_LABELS) {
				break;
			}
		}
		return new ArrayList<>(deduped.values());
	}

	private void validateDateOrder(Instant startAt, Instant dueAt) {
		if (startAt != null && dueAt != null && startAt.isAfter(dueAt)) {
			throw new IllegalArgumentException("Start date must be before due date");
		}
	}

	private void enqueueTaskEvent(String type, Task task, UUID actorUserId) {
		Map<String, Object> payload = new LinkedHashMap<>();
		payload.put("taskId", task.getId().toString());
		payload.put("projectId", task.getProject().getId().toString());
		payload.put("workspaceId", task.getProject().getWorkspace().getId().toString());
		payload.put("parentTaskId", task.getParent() == null ? null : task.getParent().getId().toString());
		payload.put("title", task.getTitle());
		payload.put("status", task.getStatus().name());
		payload.put("priority", task.getPriority());
		payload.put("path", task.getPath());
		payload.put("depth", task.getDepth());
		payload.put("assigneeUserId", task.getAssigneeUserId() == null ? null : task.getAssigneeUserId().toString());
		payload.put("dueAt", task.getDueAt());
		payload.put("startAt", task.getStartAt());
		payload.put("labels", labelsForTask(task.getId()));
		payload.put("actorUserId", actorUserId == null ? null : actorUserId.toString());
		outboxService.enqueue("task-events", task.getId().toString(), type, payload, "tasks");
	}

	private TaskResponse toResponse(Task t, List<String> labels) {
		return TaskResponse.builder()
				.id(t.getId())
				.projectId(t.getProject().getId())
				.parentTaskId(t.getParent() == null ? null : t.getParent().getId())
				.title(t.getTitle())
				.content(t.getContent())
				.status(t.getStatus())
				.priority(t.getPriority())
				.dueAt(t.getDueAt())
				.startAt(t.getStartAt())
				.assigneeUserId(t.getAssigneeUserId())
				.labels(labels)
				.effectiveDueAt(computeEffectiveDue(t))
				.sortOrder(t.getSortOrder())
				.path(t.getPath())
				.depth(t.getDepth())
				.version(t.getVersion())
				.createdAt(t.getCreatedAt())
				.updatedAt(t.getUpdatedAt())
				.build();
	}
}
