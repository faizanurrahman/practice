package com.practice.todo.modules.content.api;

import com.practice.todo.modules.content.api.dto.AppendBlockRequest;
import com.practice.todo.modules.content.api.dto.BlockResponse;
import com.practice.todo.modules.content.api.dto.UpdateBlockRequest;
import com.practice.todo.modules.content.application.BlockService;
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
@RequestMapping("/api/tasks/{taskId}/blocks")
@RequiredArgsConstructor
public class BlockController {

	private final BlockService blockService;

	@PostMapping
	public BlockResponse append(
			@PathVariable UUID taskId,
			@Valid @RequestBody AppendBlockRequest request,
			@AuthenticationPrincipal UserPrincipal user) {
		return blockService.append(user.getId(), taskId, request);
	}

	@GetMapping
	public List<BlockResponse> list(@PathVariable UUID taskId, @AuthenticationPrincipal UserPrincipal user) {
		return blockService.list(user.getId(), taskId);
	}

	@PatchMapping("/{blockId}")
	public BlockResponse update(
			@PathVariable UUID taskId,
			@PathVariable UUID blockId,
			@Valid @RequestBody UpdateBlockRequest request,
			@AuthenticationPrincipal UserPrincipal user) {
		return blockService.update(user.getId(), taskId, blockId, request);
	}

	@DeleteMapping("/{blockId}")
	public void delete(
			@PathVariable UUID taskId, @PathVariable UUID blockId, @AuthenticationPrincipal UserPrincipal user) {
		blockService.delete(user.getId(), taskId, blockId);
	}
}
