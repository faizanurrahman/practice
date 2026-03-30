package com.practice.todo.modules.attachment;

import com.practice.todo.modules.attachment.dto.AttachmentResponse;
import com.practice.todo.modules.storage.application.model.UploadCommand;
import com.practice.todo.modules.storage.application.model.UploadResult;
import com.practice.todo.modules.storage.application.port.ObjectStoragePort;
import com.practice.todo.modules.task.application.TaskService;
import com.practice.todo.modules.task.domain.Task;
import com.practice.todo.modules.user.application.UserService;
import com.practice.todo.modules.user.domain.User;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttachmentService {

	private final AttachmentRepository attachmentRepository;
	private final ObjectStoragePort objectStorage;
	private final TaskService taskService;
	private final UserService userService;

	@Transactional(readOnly = true)
	public List<AttachmentResponse> listForTask(UUID userId, UUID taskId) {
		taskService.get(userId, taskId);
		return attachmentRepository.findByTaskIdOrderByCreatedAtDesc(taskId).stream()
				.map(AttachmentService::toResponse)
				.collect(Collectors.toList());
	}

	@Transactional
	public AttachmentResponse upload(
			UUID userId, UUID taskId, String originalFilename, String contentType, byte[] data) {
		Task task = taskService.loadTaskEntityForUser(userId, taskId);
		User uploader = userService.getById(userId);
		UploadResult result = objectStorage.upload(new UploadCommand(data, contentType, originalFilename));
		Attachment a = new Attachment();
		a.setTask(task);
		a.setUploadedBy(uploader);
		a.setObjectKey(result.objectKey());
		a.setOriginalFilename(originalFilename != null ? originalFilename : "file");
		a.setContentType(result.contentType());
		a.setSizeBytes(result.sizeBytes());
		attachmentRepository.save(a);
		return toResponse(a);
	}

	@Transactional(readOnly = true)
	public byte[] downloadContent(UUID userId, UUID attachmentId) {
		Attachment a = loadOwned(userId, attachmentId);
		return objectStorage.getBytes(a.getObjectKey());
	}

	@Transactional
	public void delete(UUID userId, UUID attachmentId) {
		Attachment a = loadOwned(userId, attachmentId);
		objectStorage.delete(a.getObjectKey());
		attachmentRepository.delete(a);
	}

	private Attachment loadOwned(UUID userId, UUID attachmentId) {
		Attachment a = attachmentRepository
				.findById(attachmentId)
				.orElseThrow(() -> new IllegalArgumentException("Attachment not found"));
		taskService.get(userId, a.getTask().getId());
		return a;
	}

	private static AttachmentResponse toResponse(Attachment a) {
		return AttachmentResponse.builder()
				.id(a.getId())
				.taskId(a.getTask().getId())
				.originalFilename(a.getOriginalFilename())
				.contentType(a.getContentType())
				.sizeBytes(a.getSizeBytes())
				.createdAt(a.getCreatedAt())
				.build();
	}
}
