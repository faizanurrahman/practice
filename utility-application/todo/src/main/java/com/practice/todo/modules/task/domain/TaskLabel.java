package com.practice.todo.modules.task.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "task_labels")
@IdClass(TaskLabelId.class)
@Getter
@Setter
@NoArgsConstructor
public class TaskLabel {

	@Id
	@Column(name = "task_id", nullable = false)
	private java.util.UUID taskId;

	@Id
	@Column(name = "label_id", nullable = false)
	private java.util.UUID labelId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "task_id", insertable = false, updatable = false)
	private Task task;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "label_id", insertable = false, updatable = false)
	private Label label;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@PrePersist
	void prePersist() {
		if (createdAt == null) {
			createdAt = Instant.now();
		}
	}
}
