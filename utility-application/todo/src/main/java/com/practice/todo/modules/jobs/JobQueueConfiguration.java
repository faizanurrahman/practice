package com.practice.todo.modules.jobs;

import com.practice.todo.config.AppProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JobQueueConfiguration {

	@Bean
	@Primary
	JobQueuePort jobQueuePort(JobQueueStrategyFactory factory, AppProperties appProperties) {
		JobMode mode = JobMode.from(appProperties.getJobs().getMode());
		return factory.resolve(mode);
	}
}
