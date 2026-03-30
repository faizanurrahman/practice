package com.practice.todo.modules.storage.application;

import com.practice.todo.config.AppProperties;
import com.practice.todo.modules.storage.application.model.StorageMode;
import com.practice.todo.modules.storage.application.port.ObjectStoragePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class StorageConfiguration {

	@Bean
	@Primary
	ObjectStoragePort objectStoragePort(StorageStrategyFactory factory, AppProperties appProperties) {
		StorageMode mode = StorageMode.from(appProperties.getStorage().getMode());
		return factory.resolve(mode);
	}
}
