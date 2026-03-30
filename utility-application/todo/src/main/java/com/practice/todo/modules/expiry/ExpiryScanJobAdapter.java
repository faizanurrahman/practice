package com.practice.todo.modules.expiry;

import com.practice.todo.modules.jobs.ScanExpiryPort;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpiryScanJobAdapter implements ScanExpiryPort {

	private final ExpiryScanService expiryScanService;

	@Override
	public void scanAndPublish(Instant now) {
		expiryScanService.scanAndPublish(now);
	}
}
