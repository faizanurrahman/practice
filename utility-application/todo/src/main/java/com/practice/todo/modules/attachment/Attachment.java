package com.practice.todo.modules.attachment;

import com.practice.todo.modules.task.domain.Task;
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
@Table(name = "attachments")
@Getter
@Setter
@NoArgsConstructor
public class Attachment {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "task_id", nullable = false)
	private Task task;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "uploaded_by_user_id", nullable = false)
	private User uploadedBy;

	@Column(name = "object_key", nullable = false, length = 512)
	private String objectKey;

	@Column(name = "original_filename", nullable = false, length = 500)
	private String originalFilename;

	@Column(name = "content_type", length = 200)
	private String contentType;

	@Column(name = "size_bytes", nullable = false)
	private long sizeBytes;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@jakarta.persistence.PrePersist
	void prePersist() {
		if (createdAt == null) {
			createdAt = Instant.now();
		}
	}
}
