package com.practice.todo.modules.task.api;

import com.practice.todo.modules.task.api.dto.TaskCreateRequest;
import com.practice.todo.modules.task.api.dto.TaskReorderRequest;
import com.practice.todo.modules.task.api.dto.TaskResponse;
import com.practice.todo.modules.task.application.TaskService;
import com.practice.todo.security.UserPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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

	@PostMapping("/projects/{projectId}/tasks/reorder")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void reorder(
			@AuthenticationPrincipal UserPrincipal principal,
			@PathVariable UUID projectId,
			@RequestParam(required = false) UUID parentTaskId,
			@Valid @RequestBody TaskReorderRequest body) {
		taskService.reorder(principal.getId(), projectId, parentTaskId, body);
	}
}
