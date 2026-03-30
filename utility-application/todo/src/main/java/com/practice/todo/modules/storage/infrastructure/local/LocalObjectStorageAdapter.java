package com.practice.todo.modules.storage.infrastructure.local;

import com.practice.todo.config.AppProperties;
import com.practice.todo.modules.storage.application.model.PresignUploadCommand;
import com.practice.todo.modules.storage.application.model.PresignedUpload;
import com.practice.todo.modules.storage.application.model.StorageMode;
import com.practice.todo.modules.storage.application.model.UploadCommand;
import com.practice.todo.modules.storage.application.model.UploadResult;
import com.practice.todo.modules.storage.application.port.ObjectStorageAdapter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocalObjectStorageAdapter implements ObjectStorageAdapter {

	private final AppProperties appProperties;

	@Override
	public StorageMode mode() {
		return StorageMode.LOCAL;
	}

	@Override
	public UploadResult upload(UploadCommand command) {
		String key = UUID.randomUUID().toString();
		Path root = Path.of(appProperties.getStorage().getLocalRoot());
		try {
			Files.createDirectories(root);
			Path f = root.resolve(key);
			Files.write(f, command.data());
		} catch (IOException e) {
			throw new IllegalStateException("Failed to store object", e);
		}
		return new UploadResult(key, command.data().length, command.contentType());
	}

	@Override
	public byte[] getBytes(String objectKey) {
		Path f = Path.of(appProperties.getStorage().getLocalRoot()).resolve(objectKey);
		try {
			return Files.readAllBytes(f);
		} catch (IOException e) {
			throw new IllegalArgumentException("Object not found", e);
		}
	}

	@Override
	public void delete(String objectKey) {
		Path f = Path.of(appProperties.getStorage().getLocalRoot()).resolve(objectKey);
		try {
			Files.deleteIfExists(f);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to delete object", e);
		}
	}

	@Override
	public PresignedUpload createPresignedUpload(PresignUploadCommand command) {
		String key = UUID.randomUUID().toString();
		Path f = Path.of(appProperties.getStorage().getLocalRoot()).resolve(key);
		Instant expiresAt = Instant.now().plusSeconds(300);
		return new PresignedUpload(key, f.toUri().toString(), Map.of(), expiresAt);
	}

	@Override
	public String createDownloadUrl(String objectKey, Duration ttl) {
		Path f = Path.of(appProperties.getStorage().getLocalRoot()).resolve(objectKey);
		return f.toUri().toString();
	}
}
