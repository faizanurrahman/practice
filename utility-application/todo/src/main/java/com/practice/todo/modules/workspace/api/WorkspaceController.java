package com.practice.todo.modules.workspace.api;

import com.practice.todo.modules.workspace.api.dto.WorkspaceCreateRequest;
import com.practice.todo.modules.workspace.api.dto.WorkspaceResponse;
import com.practice.todo.modules.workspace.application.WorkspaceService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

	private final WorkspaceService workspaceService;

	@GetMapping
	List<WorkspaceResponse> list(@AuthenticationPrincipal UserPrincipal principal) {
		return workspaceService.listForUser(principal.getId());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	WorkspaceResponse create(
			@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody WorkspaceCreateRequest body) {
		return workspaceService.create(principal.getId(), body);
	}

	@GetMapping("/{workspaceId}")
	WorkspaceResponse get(
			@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID workspaceId) {
		return workspaceService.get(principal.getId(), workspaceId);
	}
}
