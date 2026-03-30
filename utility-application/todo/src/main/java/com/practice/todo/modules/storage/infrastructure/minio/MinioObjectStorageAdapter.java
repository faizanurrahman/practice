package com.practice.todo.modules.storage.infrastructure.minio;

import com.practice.todo.config.AppProperties;
import com.practice.todo.modules.storage.application.model.PresignUploadCommand;
import com.practice.todo.modules.storage.application.model.PresignedUpload;
import com.practice.todo.modules.storage.application.model.StorageMode;
import com.practice.todo.modules.storage.application.model.UploadCommand;
import com.practice.todo.modules.storage.application.model.UploadResult;
import com.practice.todo.modules.storage.application.port.ObjectStorageAdapter;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Http;
import io.minio.RemoveObjectArgs;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinioObjectStorageAdapter implements ObjectStorageAdapter {

	private static final int MAX_PRESIGN_SECONDS = 7 * 24 * 60 * 60;

	private final AppProperties appProperties;

	private MinioClient client() {
		AppProperties.Minio cfg = appProperties.getStorage().getMinio();
		return MinioClient.builder()
				.endpoint(cfg.getEndpoint())
				.credentials(cfg.getAccessKey(), cfg.getSecretKey())
				.build();
	}

	@Override
	public StorageMode mode() {
		return StorageMode.MINIO;
	}

	@Override
	public UploadResult upload(UploadCommand command) {
		String key = UUID.randomUUID().toString();
		AppProperties.Minio cfg = appProperties.getStorage().getMinio();
		ensureBucket(cfg);
		try (ByteArrayInputStream in = new ByteArrayInputStream(command.data())) {
			client().putObject(PutObjectArgs.builder()
					.bucket(cfg.getBucket())
					.object(key)
					.stream(in, (long) command.data().length, -1L)
					.contentType(command.contentType())
					.build());
		} catch (Exception e) {
			throw new IllegalStateException("Failed to store object in MinIO", e);
		}
		return new UploadResult(key, command.data().length, command.contentType());
	}

	@Override
	public byte[] getBytes(String objectKey) {
		AppProperties.Minio cfg = appProperties.getStorage().getMinio();
		try (InputStream in = client().getObject(GetObjectArgs.builder()
				.bucket(cfg.getBucket())
				.object(objectKey)
				.build())) {
			return in.readAllBytes();
		} catch (Exception e) {
			throw new IllegalArgumentException("Object not found", e);
		}
	}

	@Override
	public void delete(String objectKey) {
		AppProperties.Minio cfg = appProperties.getStorage().getMinio();
		try {
			client().removeObject(RemoveObjectArgs.builder()
					.bucket(cfg.getBucket())
					.object(objectKey)
					.build());
		} catch (Exception e) {
			throw new IllegalStateException("Failed to delete object", e);
		}
	}

	@Override
	public PresignedUpload createPresignedUpload(PresignUploadCommand command) {
		String key = UUID.randomUUID().toString();
		AppProperties.Minio cfg = appProperties.getStorage().getMinio();
		ensureBucket(cfg);
		int expirySeconds = clampExpiry(cfg.getPresignExpirySeconds());
		try {
			String url = client().getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
					.bucket(cfg.getBucket())
					.object(key)
					.method(Http.Method.PUT)
					.expiry(expirySeconds)
					.build());
			return new PresignedUpload(key, url, Map.of("Content-Type", command.contentType()),
					Instant.now().plusSeconds(expirySeconds));
		} catch (Exception e) {
			throw new IllegalStateException("Failed to create presigned upload URL", e);
		}
	}

	@Override
	public String createDownloadUrl(String objectKey, Duration ttl) {
		AppProperties.Minio cfg = appProperties.getStorage().getMinio();
		int expirySeconds = clampExpiry((int) ttl.toSeconds());
		try {
			return client().getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
					.bucket(cfg.getBucket())
					.object(objectKey)
					.method(Http.Method.GET)
					.expiry(expirySeconds)
					.build());
		} catch (Exception e) {
			throw new IllegalStateException("Failed to create presigned download URL", e);
		}
	}

	private void ensureBucket(AppProperties.Minio cfg) {
		try {
			boolean exists = client().bucketExists(BucketExistsArgs.builder().bucket(cfg.getBucket()).build());
			if (!exists) {
				client().makeBucket(MakeBucketArgs.builder().bucket(cfg.getBucket()).build());
			}
		} catch (Exception e) {
			throw new IllegalStateException("Failed to ensure MinIO bucket", e);
		}
	}

	private int clampExpiry(int seconds) {
		if (seconds <= 0) {
			return 900;
		}
		return Math.min(seconds, MAX_PRESIGN_SECONDS);
	}
}
