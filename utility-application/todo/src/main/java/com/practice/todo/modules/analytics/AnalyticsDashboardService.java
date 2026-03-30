package com.practice.todo.modules.analytics;

import com.practice.todo.modules.project.application.port.ProjectAccessPort;
import com.practice.todo.modules.workspace.application.port.WorkspaceMemberRepositoryPort;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnalyticsDashboardService {

	private final AnalyticsCounterRepository analyticsCounterRepository;
	private final WorkspaceMemberRepositoryPort workspaceMemberRepositoryPort;
	private final ProjectAccessPort projectAccessPort;

	@Transactional(readOnly = true)
	public AnalyticsDashboardResponse workspaceDashboard(UUID userId, UUID workspaceId, LocalDate from, LocalDate to) {
		if (!workspaceMemberRepositoryPort.existsByWorkspaceIdAndUserId(workspaceId, userId)) {
			throw new IllegalArgumentException("Workspace not accessible");
		}
		return buildDashboard("WORKSPACE", workspaceId, from, to);
	}

	@Transactional(readOnly = true)
	public AnalyticsDashboardResponse projectDashboard(UUID userId, UUID projectId, LocalDate from, LocalDate to) {
		var p = projectAccessPort.getAccessibleProject(userId, projectId);
		return buildDashboard("PROJECT", p.getId(), from, to);
	}

	private AnalyticsDashboardResponse buildDashboard(String entityType, UUID entityId, LocalDate from, LocalDate to) {
		List<AnalyticsCounter> all = analyticsCounterRepository.findByDimensionAndDimensionId(entityType, entityId);
		Map<String, Double> latest = new HashMap<>();
		List<AnalyticsDashboardResponse.SeriesRow> series = new ArrayList<>();
		LocalDate today = LocalDate.now(ZoneOffset.UTC);
		LocalDate fromEff = from != null ? from : today.minusDays(30);
		LocalDate toEff = to != null ? to : today;
		for (AnalyticsCounter c : all) {
			latest.put(c.getMetric(), (double) c.getCountValue());
			LocalDate pointDate = c.getUpdatedAt() != null
					? LocalDate.ofInstant(c.getUpdatedAt(), ZoneOffset.UTC)
					: today;
			if (!pointDate.isBefore(fromEff) && !pointDate.isAfter(toEff)) {
				series.add(new AnalyticsDashboardResponse.SeriesRow(c.getMetric(), pointDate, c.getCountValue()));
			}
		}
		if (series.isEmpty() && !latest.isEmpty()) {
			for (Map.Entry<String, Double> e : latest.entrySet()) {
				series.add(new AnalyticsDashboardResponse.SeriesRow(e.getKey(), today, e.getValue()));
			}
		}
		return new AnalyticsDashboardResponse(entityId, entityType, series, latest);
	}
}
