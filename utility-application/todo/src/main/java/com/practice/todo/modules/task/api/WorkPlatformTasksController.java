package com.practice.todo.modules.task.api;

import com.practice.todo.modules.task.api.workplatform.WpAddLabelRequest;
import com.practice.todo.modules.task.api.workplatform.WpCreateTaskRequest;
import com.practice.todo.modules.task.api.workplatform.WpDecisionRequest;
import com.practice.todo.modules.task.api.workplatform.WpJoinTaskRequestBody;
import com.practice.todo.modules.task.api.workplatform.WpMoveTaskRequest;
import com.practice.todo.modules.task.api.workplatform.WpProposeSubtaskRequest;
import com.practice.todo.modules.task.api.workplatform.WpTaskMemberResponse;
import com.practice.todo.modules.task.api.workplatform.WpTaskResponse;
import com.practice.todo.modules.task.api.workplatform.WpUpdateTaskRequest;
import com.practice.todo.modules.task.application.WorkPlatformTaskService;
import com.practice.todo.modules.task.domain.SubtaskProposal;
import com.practice.todo.modules.task.domain.TaskJoinRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class WorkPlatformTasksController {

	private final WorkPlatformTaskService workPlatformTaskService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public WpTaskResponse create(
			@Valid @RequestBody WpCreateTaskRequest body, @AuthenticationPrincipal UserPrincipal principal) {
		return workPlatformTaskService.create(principal.getId(), body);
	}

	@GetMapping
	public List<WpTaskResponse> listByProject(
			@RequestParam UUID projectId, @AuthenticationPrincipal UserPrincipal principal) {
		return workPlatformTaskService.listByProject(principal.getId(), projectId);
	}

	@GetMapping("/my")
	public List<WpTaskResponse> myTasks(@AuthenticationPrincipal UserPrincipal principal) {
		return workPlatformTaskService.listMyAssigned(principal.getId());
	}

	@GetMapping("/{taskId}")
	public WpTaskResponse getById(
			@PathVariable UUID taskId, @AuthenticationPrincipal UserPrincipal principal) {
		return workPlatformTaskService.get(principal.getId(), taskId);
	}

	@GetMapping("/{taskId}/tree")
	public List<WpTaskResponse> tree(
			@PathVariable UUID taskId, @AuthenticationPrincipal UserPrincipal principal) {
		return workPlatformTaskService.tree(principal.getId(), taskId);
	}

	@GetMapping("/{taskId}/members")
	public List<WpTaskMemberResponse> members(
			@PathVariable UUID taskId, @AuthenticationPrincipal UserPrincipal principal) {
		return workPlatformTaskService.listMembers(principal.getId(), taskId);
	}

	@GetMapping("/{taskId}/join-requests/pending")
	public List<TaskJoinRequest> pendingJoinRequests(
			@PathVariable UUID taskId, @AuthenticationPrincipal UserPrincipal principal) {
		return workPlatformTaskService.pendingJoinRequests(principal.getId(), taskId);
	}

	@GetMapping("/{taskId}/subtask-proposals/pending")
	public List<SubtaskProposal> pendingProposals(
			@PathVariable UUID taskId, @AuthenticationPrincipal UserPrincipal principal) {
		return workPlatformTaskService.pendingSubtaskProposals(principal.getId(), taskId);
	}

	@PatchMapping("/{taskId}")
	public WpTaskResponse update(
			@PathVariable UUID taskId,
			@RequestBody WpUpdateTaskRequest body,
			@AuthenticationPrincipal UserPrincipal principal) {
		return workPlatformTaskService.update(principal.getId(), taskId, body);
	}

	@PostMapping("/{taskId}/move")
	public WpTaskResponse move(
			@PathVariable UUID taskId,
			@RequestBody WpMoveTaskRequest body,
			@AuthenticationPrincipal UserPrincipal principal) {
		return workPlatformTaskService.move(principal.getId(), taskId, body);
	}

	@PostMapping("/{taskId}/complete")
	public WpTaskResponse complete(
			@PathVariable UUID taskId, @AuthenticationPrincipal UserPrincipal principal) {
		return workPlatformTaskService.complete(principal.getId(), taskId);
	}

	@DeleteMapping("/{taskId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable UUID taskId, @AuthenticationPrincipal UserPrincipal principal) {
		workPlatformTaskService.delete(principal.getId(), taskId);
	}

	@PostMapping("/{taskId}/restore")
	public WpTaskResponse restore(
			@PathVariable UUID taskId, @AuthenticationPrincipal UserPrincipal principal) {
		return workPlatformTaskService.restore(principal.getId(), taskId);
	}

	@PostMapping("/{taskId}/labels")
	@ResponseStatus(HttpStatus.CREATED)
	public void addLabel(
			@PathVariable UUID taskId,
			@Valid @RequestBody WpAddLabelRequest body,
			@AuthenticationPrincipal UserPrincipal principal) {
		workPlatformTaskService.addLabel(principal.getId(), taskId, body.name(), body.color());
	}

	@DeleteMapping("/{taskId}/labels/{labelId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeLabel(
			@PathVariable UUID taskId,
			@PathVariable UUID labelId,
			@AuthenticationPrincipal UserPrincipal principal) {
		workPlatformTaskService.removeLabel(principal.getId(), taskId, labelId);
	}

	@PostMapping("/{taskId}/join-requests")
	@ResponseStatus(HttpStatus.CREATED)
	public TaskJoinRequest requestJoin(
			@PathVariable UUID taskId,
			@RequestBody(required = false) WpJoinTaskRequestBody body,
			@AuthenticationPrincipal UserPrincipal principal) {
		String message = body != null ? body.message() : null;
		return workPlatformTaskService.requestJoin(principal.getId(), taskId, message);
	}

	@PostMapping("/join-requests/{requestId}/decision")
	public TaskJoinRequest decideJoin(
			@PathVariable UUID requestId,
			@Valid @RequestBody WpDecisionRequest body,
			@AuthenticationPrincipal UserPrincipal principal) {
		return workPlatformTaskService.decideJoin(principal.getId(), requestId, body.approved());
	}

	@PostMapping("/{parentTaskId}/subtask-proposals")
	@ResponseStatus(HttpStatus.CREATED)
	public SubtaskProposal proposeSubtask(
			@PathVariable UUID parentTaskId,
			@Valid @RequestBody WpProposeSubtaskRequest body,
			@AuthenticationPrincipal UserPrincipal principal) {
		return workPlatformTaskService.proposeSubtask(
				principal.getId(), parentTaskId, body.title(), body.description());
	}

	@PostMapping("/subtask-proposals/{proposalId}/decision")
	public SubtaskProposal decideProposal(
			@PathVariable UUID proposalId,
			@Valid @RequestBody WpDecisionRequest body,
			@AuthenticationPrincipal UserPrincipal principal) {
		return workPlatformTaskService.decideSubtaskProposal(
				principal.getId(), proposalId, body.approved());
	}
}
