package com.practice.todo.modules.presence.api;

import com.practice.todo.modules.presence.application.UserOnlineStatusService;
import com.practice.todo.security.UserPrincipal;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/presence")
@RequiredArgsConstructor
public class UserPresenceController {

	private final UserOnlineStatusService userOnlineStatusService;

	@GetMapping("/users/online")
	public Set<UUID> listOnlineUsers() {
		return userOnlineStatusService.getOnlineUsers();
	}

	@PostMapping("/heartbeat")
	public void heartbeat(@AuthenticationPrincipal UserPrincipal user) {
		userOnlineStatusService.heartbeat(user.getId());
	}
}
