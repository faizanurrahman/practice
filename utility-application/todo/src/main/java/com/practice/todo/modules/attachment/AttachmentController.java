package com.practice.todo.modules.attachment;

import com.practice.todo.modules.attachment.dto.AttachmentResponse;
import com.practice.todo.security.UserPrincipal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AttachmentController {

	private final AttachmentService attachmentService;

	@GetMapping("/tasks/{taskId}/attachments")
	List<AttachmentResponse> list(
			@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID taskId) {
		return attachmentService.listForTask(principal.getId(), taskId);
	}

	@PostMapping("/tasks/{taskId}/attachments")
	@ResponseStatus(HttpStatus.CREATED)
	AttachmentResponse upload(
			@AuthenticationPrincipal UserPrincipal principal,
			@PathVariable UUID taskId,
			@RequestParam("file") MultipartFile file)
			throws java.io.IOException {
		return attachmentService.upload(
				principal.getId(),
				taskId,
				file.getOriginalFilename(),
				file.getContentType(),
				file.getBytes());
	}

	@GetMapping("/attachments/{attachmentId}/download")
	ResponseEntity<Resource> download(
			@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID attachmentId) {
		byte[] bytes = attachmentService.downloadContent(principal.getId(), attachmentId);
		ByteArrayResource body = new ByteArrayResource(bytes);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"download\"")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.contentLength(bytes.length)
				.body(body);
	}

	@DeleteMapping("/attachments/{attachmentId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void delete(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID attachmentId) {
		attachmentService.delete(principal.getId(), attachmentId);
	}
}
