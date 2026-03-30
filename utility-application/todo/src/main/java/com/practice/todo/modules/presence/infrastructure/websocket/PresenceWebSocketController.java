package com.practice.todo.modules.presence.infrastructure.websocket;

import com.practice.todo.modules.presence.application.TaskPresenceService;
import com.practice.todo.security.UserPrincipal;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PresenceWebSocketController {

	private static final Pattern TASK_PRESENCE_DEST =
			Pattern.compile("^/topic/tasks/([0-9a-fA-F-]{36})/presence$");

	private final TaskPresenceService taskPresenceService;
	private final TaskPresenceSubscriptionTracker subscriptionTracker;
	private final StompUserPrincipalResolver userResolver;

	@EventListener
	public void onSubscribe(SessionSubscribeEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		String destination = accessor.getDestination();
		if (destination == null) {
			return;
		}
		Matcher matcher = TASK_PRESENCE_DEST.matcher(destination);
		if (!matcher.matches()) {
			return;
		}
		UUID taskId = UUID.fromString(matcher.group(1));
		Optional<UserPrincipal> userOpt = userResolver.resolve(accessor);
		if (userOpt.isEmpty()) {
			log.warn("presence subscribe ignored: unauthenticated sessionId={}", accessor.getSessionId());
			return;
		}
		UserPrincipal user = userOpt.get();
		String sessionId = accessor.getSessionId();
		subscriptionTracker.track(sessionId, taskId);
		taskPresenceService.join(taskId, user.getId(), displayName(user));
	}

	@EventListener
	public void onUnsubscribe(SessionUnsubscribeEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		String destination = accessor.getDestination();
		if (destination == null) {
			return;
		}
		Matcher matcher = TASK_PRESENCE_DEST.matcher(destination);
		if (!matcher.matches()) {
			return;
		}
		UUID taskId = UUID.fromString(matcher.group(1));
		Optional<UserPrincipal> userOpt = userResolver.resolve(accessor);
		if (userOpt.isEmpty()) {
			return;
		}
		String sessionId = accessor.getSessionId();
		subscriptionTracker.untrack(sessionId, taskId);
		taskPresenceService.leave(taskId, userOpt.get().getId());
	}

	private static String displayName(UserPrincipal user) {
		if (user.getEmail() != null && !user.getEmail().isBlank()) {
			return user.getEmail();
		}
		return user.getId().toString();
	}
}
