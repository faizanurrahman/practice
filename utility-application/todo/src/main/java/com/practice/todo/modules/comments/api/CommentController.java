package com.practice.todo.modules.comments.api;

import com.practice.todo.modules.comments.api.dto.AddCommentRequest;
import com.practice.todo.modules.comments.api.dto.CommentResponse;
import com.practice.todo.modules.comments.api.dto.EditCommentRequest;
import com.practice.todo.modules.comments.application.CommentService;
import com.practice.todo.security.UserPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@PostMapping
	public CommentResponse add(
			@PathVariable UUID taskId,
			@Valid @RequestBody AddCommentRequest request,
			@AuthenticationPrincipal UserPrincipal user) {
		return commentService.add(user.getId(), taskId, request);
	}

	@GetMapping("/blocks/{blockId}")
	public List<CommentResponse> listForBlock(
			@PathVariable UUID taskId,
			@PathVariable UUID blockId,
			@AuthenticationPrincipal UserPrincipal user) {
		return commentService.listForBlock(user.getId(), taskId, blockId);
	}

	@GetMapping
	public List<CommentResponse> listForTask(
			@PathVariable UUID taskId, @AuthenticationPrincipal UserPrincipal user) {
		return commentService.listForTask(user.getId(), taskId);
	}

	@PatchMapping("/{commentId}")
	public CommentResponse edit(
			@PathVariable UUID taskId,
			@PathVariable UUID commentId,
			@Valid @RequestBody EditCommentRequest request,
			@AuthenticationPrincipal UserPrincipal user) {
		return commentService.edit(user.getId(), taskId, commentId, request);
	}

	@DeleteMapping("/{commentId}")
	public void delete(
			@PathVariable UUID taskId,
			@PathVariable UUID commentId,
			@AuthenticationPrincipal UserPrincipal user) {
		commentService.delete(user.getId(), taskId, commentId);
	}
}
