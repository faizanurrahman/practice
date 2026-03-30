package com.practice.todo.modules.search;

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
@Table(name = "search_documents")
@Getter
@Setter
@NoArgsConstructor
public class SearchDocument {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "doc_type", nullable = false, length = 32)
	private String docType;

	@Column(name = "ref_id", nullable = false)
	private UUID refId;

	@Column(nullable = false, columnDefinition = "text")
	private String title;

	@Column(name = "workspace_id")
	private UUID workspaceId;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@jakarta.persistence.PrePersist
	@jakarta.persistence.PreUpdate
	void touch() {
		updatedAt = Instant.now();
	}
}
