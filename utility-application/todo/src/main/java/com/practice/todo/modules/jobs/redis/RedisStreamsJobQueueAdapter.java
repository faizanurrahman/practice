package com.practice.todo.modules.jobs.redis;

import com.practice.todo.modules.jobs.JobMode;
import com.practice.todo.modules.jobs.JobQueueAdapter;
import com.practice.todo.modules.jobs.JobRequest;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class RedisStreamsJobQueueAdapter implements JobQueueAdapter {

	@Override
	public JobMode mode() {
		return JobMode.REDIS;
	}

	@Override
	public String enqueue(JobRequest request) {
		throw new UnsupportedOperationException("Redis Streams job adapter not implemented yet");
	}

	@Override
	public String scheduleAt(JobRequest request, Instant runAt) {
		throw new UnsupportedOperationException("Redis Streams job adapter not implemented yet");
	}
}
