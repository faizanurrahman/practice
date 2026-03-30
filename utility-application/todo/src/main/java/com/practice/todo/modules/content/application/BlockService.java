package com.practice.todo.modules.content.application;

import com.practice.todo.modules.content.api.dto.AppendBlockRequest;
import com.practice.todo.modules.content.api.dto.BlockResponse;
import com.practice.todo.modules.content.api.dto.UpdateBlockRequest;
import com.practice.todo.modules.content.domain.BlockType;
import com.practice.todo.modules.content.domain.TaskBlock;
import com.practice.todo.modules.content.infrastructure.persistence.TaskBlockJpaRepository;
import com.practice.todo.modules.project.application.port.ProjectAccessPort;
import com.practice.todo.modules.task.application.port.TaskRepositoryPort;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlockService {

	private final TaskBlockJpaRepository taskBlockRepository;
	private final TaskRepositoryPort taskRepository;
	private final ProjectAccessPort projectAccessPort;

	@Transactional
	public BlockResponse append(UUID userId, UUID taskId, AppendBlockRequest req) {
		var task = taskRepository
				.findByIdWithProject(taskId)
				.filter(t -> !t.isDeleted())
				.orElseThrow(() -> new IllegalArgumentException("Task not found"));
		projectAccessPort.getAccessibleProject(userId, task.getProject().getId());
		BlockType type;
		try {
			type = BlockType.valueOf(req.blockType().trim().toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid block type");
		}
		int nextOrder = (int) taskBlockRepository.countByTaskIdAndDeletedAtIsNull(taskId) + 1;
		TaskBlock b = new TaskBlock();
		b.setId(UUID.randomUUID());
		b.setTaskId(taskId);
		b.setOwnerUserId(userId);
		b.setBlockType(type);
		b.setContent(req.content());
		b.setSortOrder(nextOrder);
		taskBlockRepository.save(b);
		return toResponse(b);
	}

	@Transactional(readOnly = true)
	public List<BlockResponse> list(UUID userId, UUID taskId) {
		var task = taskRepository
				.findByIdWithProject(taskId)
				.filter(t -> !t.isDeleted())
				.orElseThrow(() -> new IllegalArgumentException("Task not found"));
		projectAccessPort.getAccessibleProject(userId, task.getProject().getId());
		return taskBlockRepository.findByTaskIdAndDeletedAtIsNullOrderBySortOrderAsc(taskId).stream()
				.map(BlockService::toResponse)
				.toList();
	}

	@Transactional
	public BlockResponse update(UUID userId, UUID taskId, UUID blockId, UpdateBlockRequest req) {
		TaskBlock b = loadOwnedBlock(userId, taskId, blockId);
		if (!b.isOwnedBy(userId)) {
			throw new IllegalArgumentException("Only the block owner can edit this block.");
		}
		b.setContent(req.content());
		taskBlockRepository.save(b);
		return toResponse(b);
	}

	@Transactional
	public void delete(UUID userId, UUID taskId, UUID blockId) {
		TaskBlock b = loadOwnedBlock(userId, taskId, blockId);
		if (!b.isOwnedBy(userId)) {
			throw new IllegalArgumentException("Only the block owner can delete this block.");
		}
		b.setDeletedAt(java.time.Instant.now());
		taskBlockRepository.save(b);
	}

	private TaskBlock loadOwnedBlock(UUID userId, UUID taskId, UUID blockId) {
		var task = taskRepository
				.findByIdWithProject(taskId)
				.filter(t -> !t.isDeleted())
				.orElseThrow(() -> new IllegalArgumentException("Task not found"));
		projectAccessPort.getAccessibleProject(userId, task.getProject().getId());
		return taskBlockRepository
				.findByIdAndTaskIdAndDeletedAtIsNull(blockId, taskId)
				.orElseThrow(() -> new IllegalArgumentException("Block not found"));
	}

	private static BlockResponse toResponse(TaskBlock b) {
		return new BlockResponse(
				b.getId(),
				b.getTaskId(),
				b.getOwnerUserId(),
				b.getBlockType().name(),
				b.getContent(),
				b.getSortOrder(),
				b.getCreatedAt(),
				b.getUpdatedAt());
	}
}
