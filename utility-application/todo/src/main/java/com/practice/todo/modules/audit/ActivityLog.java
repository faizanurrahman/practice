package com.practice.todo.modules.audit;

import com.practice.todo.modules.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "activity_logs")
@Getter
@Setter
@NoArgsConstructor
public class ActivityLog {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "actor_user_id")
	private User actor;

	@Column(nullable = false, length = 64)
	private String action;

	@Column(name = "entity_type", nullable = false, length = 64)
	private String entityType;

	@Column(name = "entity_id", nullable = false)
	private UUID entityId;

	@Column(columnDefinition = "text")
	private String metadata;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@jakarta.persistence.PrePersist
	void prePersist() {
		if (createdAt == null) {
			createdAt = Instant.now();
		}
	}
}
