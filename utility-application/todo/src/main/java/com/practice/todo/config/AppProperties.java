package com.practice.todo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class AppProperties {

	private Cors cors = new Cors();

	/** Fixed delay between expiry scans in milliseconds */
	private long expiryScanMs = 60_000L;

	@Getter
	@Setter
	public static class Cors {
		private String allowedOrigins = "http://localhost:5173,http://localhost:4200";
	}
}
