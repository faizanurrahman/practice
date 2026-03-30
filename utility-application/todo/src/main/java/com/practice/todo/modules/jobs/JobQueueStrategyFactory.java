package com.practice.todo.modules.jobs;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class JobQueueStrategyFactory {

	private final Map<JobMode, JobQueueAdapter> adapters = new EnumMap<>(JobMode.class);

	public JobQueueStrategyFactory(List<JobQueueAdapter> adapters) {
		for (JobQueueAdapter adapter : adapters) {
			this.adapters.put(adapter.mode(), adapter);
		}
	}

	public JobQueuePort resolve(JobMode mode) {
		JobQueueAdapter adapter = adapters.get(mode);
		if (adapter == null) {
			throw new IllegalStateException("No job queue adapter registered for " + mode);
		}
		return adapter;
	}
}
