package com.practice.todo.modules.content.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "task_blocks")
@Getter
@Setter
@NoArgsConstructor
public class TaskBlock {

	@Id
	@Column(nullable = false)
	private UUID id;

	@Column(name = "task_id", nullable = false)
	private UUID taskId;

	@Column(name = "owner_user_id", nullable = false)
	private UUID ownerUserId;

	@Enumerated(EnumType.STRING)
	@Column(name = "block_type", nullable = false, length = 32)
	private BlockType blockType;

	@Column(nullable = false, columnDefinition = "text")
	private String content;

	@Column(name = "sort_order", nullable = false)
	private int sortOrder;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@Column(name = "deleted_at")
	private Instant deletedAt;

	@PrePersist
	void prePersist() {
		Instant now = Instant.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	void preUpdate() {
		updatedAt = Instant.now();
	}

	public boolean isDeleted() {
		return deletedAt != null;
	}

	public boolean isOwnedBy(UUID userId) {
		return ownerUserId != null && ownerUserId.equals(userId);
	}
}
