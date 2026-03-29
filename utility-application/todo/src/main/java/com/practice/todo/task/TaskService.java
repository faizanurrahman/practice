package com.practice.todo.task;

import com.practice.todo.project.Project;
import com.practice.todo.project.ProjectService;
import com.practice.todo.task.dto.TaskCreateRequest;
import com.practice.todo.task.dto.TaskMoveRequest;
import com.practice.todo.task.dto.TaskPatchRequest;
import com.practice.todo.task.dto.TaskReorderRequest;
import com.practice.todo.task.dto.TaskResponse;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

	private final TaskRepository taskRepository;
	private final ProjectService projectService;

	@Transactional(readOnly = true)
	public List<TaskResponse> list(UUID userId, UUID projectId, UUID parentTaskId) {
		projectService.getOwnedProject(userId, projectId);
		if (parentTaskId == null) {
			return taskRepository.findRootsByProjectId(projectId).stream()
					.map(this::toResponse)
					.collect(Collectors.toList());
		}
		Task parent = taskRepository
				.findByIdAndDeletedAtIsNull(parentTaskId)
				.orElseThrow(() -> new IllegalArgumentException("Parent task not found"));
		if (!parent.getProject().getId().equals(projectId)) {
			throw new IllegalArgumentException("Parent task is not in this project");
		}
		return taskRepository.findChildren(parentTaskId).stream()
				.map(this::toResponse)
				.collect(Collectors.toList());
	}

	@Transactional
	public TaskResponse create(UUID userId, UUID projectId, TaskCreateRequest req) {
		Project p = projectService.getOwnedProject(userId, projectId);
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
		task.setStatus(req.getStatus() != null ? req.getStatus() : TaskStatus.TODO);
		task.setSortOrder(nextSortOrder(projectId, req.getParentTaskId()));
		taskRepository.save(task);
		return toResponse(task);
	}

	@Transactional(readOnly = true)
	public TaskResponse get(UUID userId, UUID taskId) {
		return toResponse(loadForUser(userId, taskId));
	}

	@Transactional(readOnly = true)
	public List<TaskResponse> subtree(UUID userId, UUID taskId, int maxDepth) {
		Task root = loadForUser(userId, taskId);
		List<TaskResponse> out = new ArrayList<>();
		collectSubtree(root, 0, Math.max(0, maxDepth), out);
		return out;
	}

	private void collectSubtree(Task node, int depth, int maxDepth, List<TaskResponse> out) {
		if (depth > maxDepth) {
			return;
		}
		out.add(toResponse(node));
		if (depth == maxDepth) {
			return;
		}
		for (Task c : taskRepository.findChildren(node.getId())) {
			collectSubtree(c, depth + 1, maxDepth, out);
		}
	}

	@Transactional
	public TaskResponse patch(UUID userId, UUID taskId, TaskPatchRequest req) {
		Task t = loadForUser(userId, taskId);
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
		if (req.getStatus() != null) {
			if (req.getStatus() == TaskStatus.DONE && anyIncompleteDescendant(taskId)) {
				throw new IllegalArgumentException("Cannot complete task while a descendant is incomplete");
			}
			t.setStatus(req.getStatus());
		}
		taskRepository.save(t);
		return toResponse(t);
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
		List<UUID> ids = collectSubtreeIds(t.getId());
		taskRepository.markDeleted(ids, Instant.now());
	}

	@Transactional
	public TaskResponse move(UUID userId, UUID taskId, TaskMoveRequest req) {
		Task t = loadForUser(userId, taskId);
		UUID projectId = t.getProject().getId();
		UUID newParentId = req.getNewParentTaskId();
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
			if (newParentInsideMovedSubtree(taskId, newParentId)) {
				throw new IllegalArgumentException("Cannot move task under its own descendant");
			}
			t.setParent(parent);
		} else {
			t.setParent(null);
		}
		int next = nextSortOrderExcluding(projectId, newParentId, taskId);
		t.setSortOrder(next);
		taskRepository.save(t);
		return toResponse(t);
	}

	@Transactional
	public void reorder(UUID userId, UUID projectId, UUID parentTaskId, TaskReorderRequest req) {
		projectService.getOwnedProject(userId, projectId);
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
			UUID pp = task.getParent() == null ? null : task.getParent().getId();
			if (!Objects.equals(pp, parentTaskId)) {
				throw new IllegalArgumentException("Task is not a sibling under the given parent");
			}
			task.setSortOrder(i);
		}
	}

	private Task loadForUser(UUID userId, UUID taskId) {
		Task t = taskRepository
				.findByIdWithProject(taskId)
				.orElseThrow(() -> new IllegalArgumentException("Task not found"));
		if (!t.getProject().getOwner().getId().equals(userId)) {
			throw new IllegalArgumentException("Task not found");
		}
		return t;
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

	/**
	 * True if {@code nodeId} is {@code movedRootId} or a descendant of it (walk up from nodeId).
	 */
	private boolean newParentInsideMovedSubtree(UUID movedRootId, UUID nodeId) {
		UUID cur = nodeId;
		while (cur != null) {
			if (cur.equals(movedRootId)) {
				return true;
			}
			Task n = taskRepository.findByIdAndDeletedAtIsNull(cur).orElse(null);
			if (n == null) {
				break;
			}
			cur = n.getParent() == null ? null : n.getParent().getId();
		}
		return false;
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

	private TaskResponse toResponse(Task t) {
		return TaskResponse.builder()
				.id(t.getId())
				.projectId(t.getProject().getId())
				.parentTaskId(t.getParent() == null ? null : t.getParent().getId())
				.title(t.getTitle())
				.content(t.getContent())
				.status(t.getStatus())
				.dueAt(t.getDueAt())
				.effectiveDueAt(computeEffectiveDue(t))
				.sortOrder(t.getSortOrder())
				.createdAt(t.getCreatedAt())
				.updatedAt(t.getUpdatedAt())
				.build();
	}
}
