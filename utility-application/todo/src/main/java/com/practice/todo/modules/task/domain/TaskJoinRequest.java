package com.practice.todo.modules.task.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "task_join_requests")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskJoinRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "task_id", nullable = false)
	@Getter(AccessLevel.NONE)
	@JsonIgnore
	private Task task;

	@Column(name = "requester_id", nullable = false)
	private UUID requesterId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 16)
	private JoinRequestStatus status = JoinRequestStatus.PENDING;

	@Column(columnDefinition = "text")
	private String message;

	@Column(name = "requested_at", nullable = false)
	private Instant requestedAt;

	@Column(name = "decided_at")
	private Instant decidedAt;

	@Column(name = "decided_by_user_id")
	private UUID decidedByUserId;

	@PrePersist
	void prePersist() {
		if (requestedAt == null) {
			requestedAt = Instant.now();
		}
	}

	@JsonProperty("taskId")
	public UUID getTaskId() {
		return task == null ? null : task.getId();
	}

	@JsonIgnore
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}
}
