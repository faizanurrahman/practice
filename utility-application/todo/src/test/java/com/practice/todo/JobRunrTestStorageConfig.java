package com.practice.todo;

import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.storage.StorageProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class JobRunrTestStorageConfig {

	@Bean
	StorageProvider jobRunrStorageProvider() {
		return new InMemoryStorageProvider();
	}
}
