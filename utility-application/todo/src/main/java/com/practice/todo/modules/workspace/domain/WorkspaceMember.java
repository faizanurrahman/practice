package com.practice.todo.modules.workspace.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "workspace_members")
@IdClass(WorkspaceMemberId.class)
@Getter
@Setter
@NoArgsConstructor
public class WorkspaceMember {

	@Id
	@Column(name = "workspace_id", nullable = false)
	private UUID workspaceId;

	@Id
	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private WorkspaceRole role;

	@Column(name = "joined_at", nullable = false)
	private Instant joinedAt;
}
