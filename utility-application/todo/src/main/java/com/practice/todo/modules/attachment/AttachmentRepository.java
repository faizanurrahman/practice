package com.practice.todo.modules.attachment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

	List<Attachment> findByTaskIdOrderByCreatedAtDesc(UUID taskId);

	Optional<Attachment> findByIdAndTaskId(UUID id, UUID taskId);
}
