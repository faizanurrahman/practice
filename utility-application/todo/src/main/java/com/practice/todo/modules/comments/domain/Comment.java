package com.practice.todo.modules.comments.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
public class Comment {

	@Id
	@Column(nullable = false)
	private UUID id;

	@Column(name = "block_id", nullable = false)
	private UUID blockId;

	@Column(name = "task_id", nullable = false)
	private UUID taskId;

	@Column(name = "author_user_id", nullable = false)
	private UUID authorUserId;

	@Column(name = "parent_comment_id")
	private UUID parentCommentId;

	@Column(nullable = false, columnDefinition = "text")
	private String body;

	@Column(nullable = false, length = 2000)
	private String path;

	@Column(nullable = false)
	private int depth;

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

	public boolean isAuthoredBy(UUID userId) {
		return authorUserId != null && authorUserId.equals(userId);
	}
}
