package com.practice.todo.modules.eventing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
public class OutboxEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, length = 200)
	private String topic;

	@Column(name = "event_id", nullable = false)
	private UUID eventId;

	@Column(name = "partition_key", length = 200)
	private String partitionKey;

	@Column(name = "event_type", nullable = false, length = 200)
	private String eventType;

	@Column(name = "event_version", nullable = false)
	private int eventVersion;

	@Column(name = "source", length = 200)
	private String source;

	@Column(name = "payload_json", nullable = false, columnDefinition = "text")
	private String payloadJson;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "published_at")
	private Instant publishedAt;

	@Version
	@Column(nullable = false)
	private long version;

	@Column(name = "attempts", nullable = false)
	private int attempts;

	@Column(name = "last_error", columnDefinition = "text")
	private String lastError;

	@Column(name = "next_attempt_at")
	private Instant nextAttemptAt;

	@jakarta.persistence.PrePersist
	void prePersist() {
		if (createdAt == null) {
			createdAt = Instant.now();
		}
		if (eventId == null) {
			eventId = UUID.randomUUID();
		}
	}
}
