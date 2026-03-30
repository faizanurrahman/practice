package com.practice.todo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class AppProperties {

	private Cors cors = new Cors();

	private Storage storage = new Storage();
	private Jobs jobs = new Jobs();

	/**
	 * Work-platform parity defaults: single deployable (no separate worker JAR). Optional Redis and
	 * Elasticsearch are enabled only when {@code spring.data.redis.*} / {@code spring.elasticsearch.uris} are set.
	 */
	private Parity parity = new Parity();

	/** Fixed delay between expiry scans in milliseconds */
	private long expiryScanMs = 60_000L;

	/** Max outbox batch size per relay tick */
	private int outboxBatchSize = 50;

	/** Base backoff for failed outbox deliveries (seconds) */
	private long outboxBackoffSeconds = 30;

	@Getter
	@Setter
	public static class Cors {
		private String allowedOrigins = "http://localhost:5173,http://localhost:4200";
	}

	@Getter
	@Setter
	public static class Storage {
		/** local | minio | s3 (s3 reserved for future adapter) */
		private String mode = "local";

		/** Root directory for {@code local} mode */
		private String localRoot = System.getProperty("java.io.tmpdir") + "/todo-objects";

		private Minio minio = new Minio();
	}

	@Getter
	@Setter
	public static class Minio {
		private String endpoint = "http://localhost:9000";
		private String accessKey = "minioadmin";
		private String secretKey = "minioadmin";
		private String bucket = "todo";
		private String region = "us-east-1";
		private boolean secure = false;
		private int presignExpirySeconds = 900;
	}

	@Getter
	@Setter
	public static class Jobs {
		/** jobrunr | redis */
		private String mode = "jobrunr";
	}

	@Getter
	@Setter
	public static class Parity {
		/** When false (default), background work runs in-process (JobRunr / scheduled), not a second Boot app. */
		private boolean separateWorkerApp = false;
	}
}
