package com.practice.todo.modules.iam;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Placeholder for outbound webhook registration (phase 5). Returns an empty list until implemented.
 */
@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	List<String> listConfiguredWebhooks() {
		return List.of();
	}
}
