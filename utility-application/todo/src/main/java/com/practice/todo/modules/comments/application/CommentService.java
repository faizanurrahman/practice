package com.practice.todo.modules.comments.application;

import com.practice.todo.modules.comments.api.dto.AddCommentRequest;
import com.practice.todo.modules.comments.api.dto.CommentResponse;
import com.practice.todo.modules.comments.api.dto.EditCommentRequest;
import com.practice.todo.modules.comments.domain.Comment;
import com.practice.todo.modules.comments.domain.CommentHierarchyService;
import com.practice.todo.modules.comments.infrastructure.persistence.CommentJpaRepository;
import com.practice.todo.modules.content.domain.TaskBlock;
import com.practice.todo.modules.content.infrastructure.persistence.TaskBlockJpaRepository;
import com.practice.todo.modules.project.application.port.ProjectAccessPort;
import com.practice.todo.modules.task.application.port.TaskRepositoryPort;
import com.practice.todo.realtime.RealTimeBroadcastPort;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentJpaRepository commentRepository;
	private final TaskBlockJpaRepository taskBlockRepository;
	private final TaskRepositoryPort taskRepository;
	private final ProjectAccessPort projectAccessPort;
	private final CommentHierarchyService hierarchyService;
	private final RealTimeBroadcastPort realTimeBroadcastPort;

	@Transactional
	public CommentResponse add(UUID userId, UUID taskId, AddCommentRequest req) {
		var task = taskRepository
				.findByIdWithProject(taskId)
				.filter(t -> !t.isDeleted())
				.orElseThrow(() -> new IllegalArgumentException("Task not found"));
		projectAccessPort.getAccessibleProject(userId, task.getProject().getId());
		TaskBlock block = taskBlockRepository
				.findByIdAndTaskIdAndDeletedAtIsNull(req.blockId(), taskId)
				.orElseThrow(() -> new IllegalArgumentException("Block not found"));
		Comment parent = null;
		if (req.parentCommentId() != null) {
			parent = commentRepository
					.findById(req.parentCommentId())
					.filter(c -> !c.isDeleted())
					.orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
			if (!parent.getBlockId().equals(req.blockId()) || !parent.getTaskId().equals(taskId)) {
				throw new IllegalArgumentException("Parent comment does not belong to this block/task.");
			}
		}
		int sortOrder = computeNextSortOrder(req.blockId(), req.parentCommentId());
		String path = hierarchyService.computePathForNewComment(parent, sortOrder);
		int depth = hierarchyService.depthForPath(path);
		Instant now = Instant.now();
		Comment c = new Comment();
		c.setId(UUID.randomUUID());
		c.setBlockId(req.blockId());
		c.setTaskId(taskId);
		c.setAuthorUserId(userId);
		c.setParentCommentId(req.parentCommentId());
		c.setBody(req.body());
		c.setPath(path);
		c.setDepth(depth);
		c.setDeletedAt(null);
		c.setCreatedAt(now);
		c.setUpdatedAt(now);
		Comment saved = commentRepository.save(c);
		realTimeBroadcastPort.broadcastToTask(taskId, "comments", commentCreatedPayload(saved, now));
		return toResponse(saved);
	}

	@Transactional(readOnly = true)
	public List<CommentResponse> listForBlock(UUID userId, UUID taskId, UUID blockId) {
		assertTaskAndBlockAccess(userId, taskId, blockId);
		return commentRepository.findByBlockIdAndDeletedAtIsNullOrderByPathAsc(blockId).stream()
				.map(CommentService::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<CommentResponse> listForTask(UUID userId, UUID taskId) {
		var task = taskRepository
				.findByIdWithProject(taskId)
				.filter(t -> !t.isDeleted())
				.orElseThrow(() -> new IllegalArgumentException("Task not found"));
		projectAccessPort.getAccessibleProject(userId, task.getProject().getId());
		return commentRepository.findByTaskIdAndDeletedAtIsNullOrderByPathAsc(taskId).stream()
				.map(CommentService::toResponse)
				.toList();
	}

	@Transactional
	public CommentResponse edit(UUID userId, UUID taskId, UUID commentId, EditCommentRequest req) {
		Comment c = commentRepository
				.findByIdAndTaskIdAndDeletedAtIsNull(commentId, taskId)
				.orElseThrow(() -> new IllegalArgumentException("Comment not found"));
		var task = taskRepository
				.findByIdWithProject(taskId)
				.filter(t -> !t.isDeleted())
				.orElseThrow(() -> new IllegalArgumentException("Task not found"));
		projectAccessPort.getAccessibleProject(userId, task.getProject().getId());
		if (!c.isAuthoredBy(userId)) {
			throw new IllegalArgumentException("Only the comment author can edit this comment.");
		}
		c.setBody(req.body());
		commentRepository.save(c);
		return toResponse(c);
	}

	@Transactional
	public void delete(UUID userId, UUID taskId, UUID commentId) {
		Comment c = commentRepository
				.findByIdAndTaskIdAndDeletedAtIsNull(commentId, taskId)
				.orElseThrow(() -> new IllegalArgumentException("Comment not found"));
		var task = taskRepository
				.findByIdWithProject(taskId)
				.filter(t -> !t.isDeleted())
				.orElseThrow(() -> new IllegalArgumentException("Task not found"));
		projectAccessPort.getAccessibleProject(userId, task.getProject().getId());
		if (!c.isAuthoredBy(userId)) {
			throw new IllegalArgumentException("Only the comment author can delete this comment.");
		}
		c.setDeletedAt(Instant.now());
		commentRepository.save(c);
	}

	private void assertTaskAndBlockAccess(UUID userId, UUID taskId, UUID blockId) {
		var task = taskRepository
				.findByIdWithProject(taskId)
				.filter(t -> !t.isDeleted())
				.orElseThrow(() -> new IllegalArgumentException("Task not found"));
		projectAccessPort.getAccessibleProject(userId, task.getProject().getId());
		taskBlockRepository
				.findByIdAndTaskIdAndDeletedAtIsNull(blockId, taskId)
				.orElseThrow(() -> new IllegalArgumentException("Block not found"));
	}

	private int computeNextSortOrder(UUID blockId, UUID parentCommentId) {
		if (parentCommentId == null) {
			return (int) commentRepository.countByBlockIdAndDeletedAtIsNullAndParentCommentIdIsNull(blockId) + 1;
		}
		return (int) commentRepository.countByBlockIdAndDeletedAtIsNullAndParentCommentId(blockId, parentCommentId)
				+ 1;
	}

	private static Map<String, Object> commentCreatedPayload(Comment saved, Instant now) {
		Map<String, Object> m = new HashMap<>();
		m.put("eventType", "comment.created");
		m.put("taskId", saved.getTaskId());
		m.put("blockId", saved.getBlockId());
		m.put("commentId", saved.getId());
		m.put("authorUserId", saved.getAuthorUserId());
		m.put("parentCommentId", saved.getParentCommentId());
		m.put("body", saved.getBody());
		m.put("path", saved.getPath());
		m.put("depth", saved.getDepth());
		m.put("occurredAt", now);
		return m;
	}

	private static CommentResponse toResponse(Comment c) {
		return new CommentResponse(
				c.getId(),
				c.getBlockId(),
				c.getTaskId(),
				c.getAuthorUserId(),
				c.getParentCommentId(),
				c.getBody(),
				c.getPath(),
				c.getDepth(),
				c.getCreatedAt(),
				c.getUpdatedAt());
	}
}
