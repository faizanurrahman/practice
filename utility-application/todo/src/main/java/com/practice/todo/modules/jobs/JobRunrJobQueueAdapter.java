package com.practice.todo.modules.jobs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jobrunr.scheduling.BackgroundJob;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobRunrJobQueueAdapter implements JobQueueAdapter {

	private final JobWorker jobWorker;
	private final ObjectMapper objectMapper;

	@Override
	public JobMode mode() {
		return JobMode.JOBRUNR;
	}

	@Override
	public String enqueue(JobRequest request) {
		String payloadJson = serialize(request);
		BackgroundJob.enqueue(() -> jobWorker.execute(payloadJson));
		return UUID.randomUUID().toString();
	}

	@Override
	public String scheduleAt(JobRequest request, Instant runAt) {
		String payloadJson = serialize(request);
		BackgroundJob.schedule(runAt, () -> jobWorker.execute(payloadJson));
		return UUID.randomUUID().toString();
	}

	private String serialize(JobRequest request) {
		try {
			return objectMapper.writeValueAsString(request);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Failed to serialize job request", e);
		}
	}
}
