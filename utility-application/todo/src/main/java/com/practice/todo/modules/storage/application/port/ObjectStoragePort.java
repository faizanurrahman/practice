package com.practice.todo.modules.storage.application.port;

import com.practice.todo.modules.storage.application.model.PresignUploadCommand;
import com.practice.todo.modules.storage.application.model.PresignedUpload;
import com.practice.todo.modules.storage.application.model.UploadCommand;
import com.practice.todo.modules.storage.application.model.UploadResult;
import java.time.Duration;

public interface ObjectStoragePort {

	UploadResult upload(UploadCommand command);

	byte[] getBytes(String objectKey);

	void delete(String objectKey);

	PresignedUpload createPresignedUpload(PresignUploadCommand command);

	String createDownloadUrl(String objectKey, Duration ttl);

	default String createDownloadUrl(String objectKey) {
		return createDownloadUrl(objectKey, Duration.ofMinutes(15));
	}
}
