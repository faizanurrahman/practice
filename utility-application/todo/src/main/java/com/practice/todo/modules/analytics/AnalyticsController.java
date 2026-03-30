package com.practice.todo.modules.analytics;

import com.practice.todo.security.UserPrincipal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

	private final AnalyticsDashboardService analyticsDashboardService;

	@GetMapping("/projects/{id}/dashboard")
	@PreAuthorize("isAuthenticated()")
	public AnalyticsDashboardResponse projectDashboard(
			@PathVariable UUID id,
			@RequestParam(required = false) LocalDate from,
			@RequestParam(required = false) LocalDate to,
			@AuthenticationPrincipal UserPrincipal user) {
		return analyticsDashboardService.projectDashboard(user.getId(), id, from, to);
	}

	@GetMapping("/workspaces/{id}/dashboard")
	@PreAuthorize("isAuthenticated()")
	public AnalyticsDashboardResponse workspaceDashboard(
			@PathVariable UUID id,
			@RequestParam(required = false) LocalDate from,
			@RequestParam(required = false) LocalDate to,
			@AuthenticationPrincipal UserPrincipal user) {
		return analyticsDashboardService.workspaceDashboard(user.getId(), id, from, to);
	}
}
