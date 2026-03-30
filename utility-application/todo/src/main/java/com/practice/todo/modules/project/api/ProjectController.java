package com.practice.todo.modules.project.api;

import com.practice.todo.modules.project.api.dto.ProjectPatchRequest;
import com.practice.todo.modules.project.api.dto.ProjectRequest;
import com.practice.todo.modules.project.api.dto.ProjectResponse;
import com.practice.todo.modules.project.application.ProjectService;
import com.practice.todo.security.UserPrincipal;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

	private final ProjectService projectService;

	@GetMapping
	List<ProjectResponse> list(@AuthenticationPrincipal UserPrincipal principal) {
		return projectService.list(principal.getId());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	ProjectResponse create(
			@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody ProjectRequest body) {
		return projectService.create(principal.getId(), body);
	}

	@GetMapping("/{projectId}")
	ProjectResponse get(
			@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID projectId) {
		return projectService.get(principal.getId(), projectId);
	}

	@PatchMapping("/{projectId}")
	ProjectResponse patch(
			@AuthenticationPrincipal UserPrincipal principal,
			@PathVariable UUID projectId,
			@Valid @RequestBody ProjectPatchRequest body) {
		return projectService.patch(principal.getId(), projectId, body);
	}

	@DeleteMapping("/{projectId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void delete(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID projectId) {
		projectService.delete(principal.getId(), projectId);
	}
}
