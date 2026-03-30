package com.practice.todo.modules.audit;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

	private final AuditQueryService auditQueryService;

	@GetMapping("/entity/{type}/{id}")
	@PreAuthorize("isAuthenticated()")
	public AuditPageResponse<ActivityLogResponse> activityForEntity(
			@PathVariable String type,
			@PathVariable UUID id,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "20") int size) {
		return AuditQueryService.toPage(auditQueryService.forEntity(type, id, page, size));
	}

	@GetMapping("/user/{userId}")
	@PreAuthorize("isAuthenticated()")
	public AuditPageResponse<ActivityLogResponse> activityForUser(
			@PathVariable UUID userId,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "20") int size) {
		return AuditQueryService.toPage(auditQueryService.forUser(userId, page, size));
	}
}
