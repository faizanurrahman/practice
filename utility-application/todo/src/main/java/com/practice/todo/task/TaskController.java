package com.practice.todo.task;

import com.practice.todo.security.UserPrincipal;
import com.practice.todo.task.dto.TaskCreateRequest;
import com.practice.todo.task.dto.TaskMoveRequest;
import com.practice.todo.task.dto.TaskPatchRequest;
import com.practice.todo.task.dto.TaskReorderRequest;
import com.practice.todo.task.dto.TaskResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskController {

	private final TaskService taskService;

	@GetMapping("/projects/{projectId}/tasks")
	List<TaskResponse> list(
			@AuthenticationPrincipal UserPrincipal principal,
			@PathVariable UUID projectId,
			@RequestParam(required = false) UUID parentTaskId) {
		return taskService.list(principal.getId(), projectId, parentTaskId);
	}

	@PostMapping("/projects/{projectId}/tasks")
	@ResponseStatus(HttpStatus.CREATED)
	TaskResponse create(
			@AuthenticationPrincipal UserPrincipal principal,
			@PathVariable UUID projectId,
			@Valid @RequestBody TaskCreateRequest body) {
		return taskService.create(principal.getId(), projectId, body);
	}

	@GetMapping("/tasks/{taskId}")
	TaskResponse get(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID taskId) {
		return taskService.get(principal.getId(), taskId);
	}

	@GetMapping("/tasks/{taskId}/subtree")
	List<TaskResponse> subtree(
			@AuthenticationPrincipal UserPrincipal principal,
			@PathVariable UUID taskId,
			@RequestParam(defaultValue = "64") int maxDepth) {
		return taskService.subtree(principal.getId(), taskId, maxDepth);
	}

	@PatchMapping("/tasks/{taskId}")
	TaskResponse patch(
			@AuthenticationPrincipal UserPrincipal principal,
			@PathVariable UUID taskId,
			@Valid @RequestBody TaskPatchRequest body) {
		return taskService.patch(principal.getId(), taskId, body);
	}

	@PostMapping("/tasks/{taskId}/complete")
	TaskResponse complete(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID taskId) {
		return taskService.complete(principal.getId(), taskId);
	}

	@PostMapping("/tasks/{taskId}/move")
	TaskResponse move(
			@AuthenticationPrincipal UserPrincipal principal,
			@PathVariable UUID taskId,
			@Valid @RequestBody TaskMoveRequest body) {
		return taskService.move(principal.getId(), taskId, body);
	}

	@PostMapping("/projects/{projectId}/tasks/reorder")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void reorder(
			@AuthenticationPrincipal UserPrincipal principal,
			@PathVariable UUID projectId,
			@RequestParam(required = false) UUID parentTaskId,
			@Valid @RequestBody TaskReorderRequest body) {
		taskService.reorder(principal.getId(), projectId, parentTaskId, body);
	}

	@DeleteMapping("/tasks/{taskId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void delete(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID taskId) {
		taskService.delete(principal.getId(), taskId);
	}
}
