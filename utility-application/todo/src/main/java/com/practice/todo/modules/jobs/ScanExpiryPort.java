package com.practice.todo.modules.jobs;

import java.time.Instant;

public interface ScanExpiryPort {

	void scanAndPublish(Instant now);
}
