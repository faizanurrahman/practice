package com.practice.todo.modules.jobs;

import java.time.Instant;

public interface JobQueuePort {

	String enqueue(JobRequest request);

	String scheduleAt(JobRequest request, Instant runAt);
}
