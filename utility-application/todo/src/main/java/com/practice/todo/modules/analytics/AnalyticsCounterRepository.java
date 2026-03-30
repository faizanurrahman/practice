package com.practice.todo.modules.analytics;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalyticsCounterRepository extends JpaRepository<AnalyticsCounter, UUID> {

	Optional<AnalyticsCounter> findByDimensionAndDimensionIdAndMetric(
			String dimension, UUID dimensionId, String metric);

	List<AnalyticsCounter> findByDimensionAndDimensionId(String dimension, UUID dimensionId);
}
