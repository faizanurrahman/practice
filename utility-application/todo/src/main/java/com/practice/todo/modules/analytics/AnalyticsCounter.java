package com.practice.todo.modules.analytics;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "analytics_counters")
@Getter
@Setter
@NoArgsConstructor
public class AnalyticsCounter {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, length = 64)
	private String dimension;

	@Column(name = "dimension_id", nullable = false)
	private UUID dimensionId;

	@Column(nullable = false, length = 64)
	private String metric;

	@Column(name = "count_value", nullable = false)
	private long countValue;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@jakarta.persistence.PrePersist
	@jakarta.persistence.PreUpdate
	void touch() {
		updatedAt = Instant.now();
	}
}
