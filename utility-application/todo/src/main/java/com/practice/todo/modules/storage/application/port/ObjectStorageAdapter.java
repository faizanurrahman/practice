package com.practice.todo.modules.storage.application.port;

import com.practice.todo.modules.storage.application.model.StorageMode;

public interface ObjectStorageAdapter extends ObjectStoragePort {

	StorageMode mode();
}
