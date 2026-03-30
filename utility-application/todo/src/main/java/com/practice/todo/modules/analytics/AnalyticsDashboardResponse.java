package com.practice.todo.modules.analytics;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record AnalyticsDashboardResponse(
		UUID entityId,
		String entityType,
		List<SeriesRow> series,
		Map<String, Double> latestByMetric) {

	public record SeriesRow(String metricType, LocalDate date, double value) {}
}
