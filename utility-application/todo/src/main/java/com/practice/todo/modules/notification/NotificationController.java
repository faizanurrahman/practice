package com.practice.todo.modules.notification;

import com.practice.todo.modules.notification.dto.NotificationResponse;
import com.practice.todo.security.UserPrincipal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping
	List<NotificationResponse> list(@AuthenticationPrincipal UserPrincipal principal) {
		return notificationService.list(principal.getId());
	}

	@PostMapping("/{id}/read")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void markRead(@AuthenticationPrincipal UserPrincipal principal, @PathVariable("id") UUID id) {
		notificationService.markRead(principal.getId(), id);
	}
}
