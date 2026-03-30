package com.practice.todo.modules.storage.application;

import com.practice.todo.modules.storage.application.model.StorageMode;
import com.practice.todo.modules.storage.application.port.ObjectStorageAdapter;
import com.practice.todo.modules.storage.application.port.ObjectStoragePort;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class StorageStrategyFactory {

	private final Map<StorageMode, ObjectStorageAdapter> adapters = new EnumMap<>(StorageMode.class);

	public StorageStrategyFactory(List<ObjectStorageAdapter> adapters) {
		for (ObjectStorageAdapter adapter : adapters) {
			this.adapters.put(adapter.mode(), adapter);
		}
	}

	public ObjectStoragePort resolve(StorageMode mode) {
		ObjectStorageAdapter adapter = adapters.get(mode);
		if (adapter == null) {
			throw new IllegalStateException("No storage adapter registered for " + mode);
		}
		return adapter;
	}
}
