package com.practice.todo.modules.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.todo.modules.notification.job.NotificationDispatchPayload;
import com.practice.todo.modules.notification.job.NotificationDispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobWorker {

	private final ScanExpiryPort scanExpiryPort;
	private final NotificationDispatchService notificationDispatchService;
	private final ObjectMapper objectMapper;

	public void execute(String payloadJson) {
		try {
			JobRequest request = objectMapper.readValue(payloadJson, JobRequest.class);
			switch (request.name()) {
				case "expiry.scan" -> {
					ExpiryScanPayload payload = objectMapper.convertValue(request.payload(), ExpiryScanPayload.class);
					scanExpiryPort.scanAndPublish(payload.now());
				}
				case "notification.send" -> {
					NotificationDispatchPayload payload =
							objectMapper.convertValue(request.payload(), NotificationDispatchPayload.class);
					notificationDispatchService.dispatch(payload);
				}
				default -> log.info("Unknown job name={} payload={}", request.name(), request.payload());
			}
		} catch (Exception ex) {
			log.warn("Job execution failed: {}", ex.getMessage());
			throw new IllegalStateException(ex);
		}
	}
}
