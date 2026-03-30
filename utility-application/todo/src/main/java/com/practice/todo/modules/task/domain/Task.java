package com.practice.todo.modules.task.domain;

import com.practice.todo.modules.project.domain.Project;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
public class Task {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_task_id")
	private Task parent;

	@Column(nullable = false, length = 500)
	private String title;

	@Column(columnDefinition = "text")
	private String content;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private TaskStatus status = TaskStatus.TODO;

	@Column(nullable = false, length = 16)
	private String priority = "MEDIUM";

	@Column(name = "due_at")
	private Instant dueAt;

	@Column(name = "start_at")
	private Instant startAt;

	@Column(name = "assignee_user_id")
	private UUID assigneeUserId;

	@Column(name = "sort_order", nullable = false)
	private int sortOrder;

	@Column(nullable = false, length = 2000)
	private String path;

	@Column(nullable = false)
	private int depth;

	@Version
	@Column(nullable = false)
	private long version;

	@Column(name = "deleted_at")
	private Instant deletedAt;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

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
}
