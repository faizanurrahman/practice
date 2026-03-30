package com.practice.todo.modules.task.domain;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subtask_proposals")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"parentTask"})
public class SubtaskProposal {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "parent_task_id", nullable = false)
	private Task parentTask;

	@Column(name = "proposer_id", nullable = false)
	private UUID proposerId;

	@Column(nullable = false, length = 500)
	private String title;

	@Column(columnDefinition = "text")
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 16)
	private ProposalStatus status = ProposalStatus.PROPOSED;

	@Column(name = "proposed_at", nullable = false)
	private Instant proposedAt;

	@Column(name = "decided_at")
	private Instant decidedAt;

	@Column(name = "decided_by_user_id")
	private UUID decidedByUserId;

	@PrePersist
	void prePersist() {
		if (proposedAt == null) {
			proposedAt = Instant.now();
		}
	}

	@JsonProperty("parentTaskId")
	public UUID getParentTaskId() {
		return parentTask == null ? null : parentTask.getId();
	}
}
