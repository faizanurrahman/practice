package com.practice.todo.modules.presence.infrastructure.websocket;

import com.practice.todo.modules.presence.application.TaskPresenceService;
import com.practice.todo.modules.presence.application.UserOnlineStatusService;
import com.practice.todo.security.UserPrincipal;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketPresenceEventListener {

	private final UserOnlineStatusService userOnlineStatusService;
	private final TaskPresenceService taskPresenceService;
	private final TaskPresenceSubscriptionTracker subscriptionTracker;
	private final StompUserPrincipalResolver userResolver;

	@EventListener
	public void onConnected(SessionConnectedEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		Optional<UserPrincipal> userOpt = userResolver.resolve(accessor);
		if (userOpt.isEmpty()) {
			log.debug("websocket connected without authenticated user sessionId={}", accessor.getSessionId());
			return;
		}
		UserPrincipal user = userOpt.get();
		try {
			userOnlineStatusService.markOnline(user.getId());
			userOnlineStatusService.heartbeat(user.getId());
		} catch (RuntimeException ex) {
			log.error("mark user online on websocket connect failed userId={}", user.getId(), ex);
		}
	}

	@EventListener
	public void onDisconnected(SessionDisconnectEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		String sessionId = accessor.getSessionId();
		Optional<UserPrincipal> userOpt = userResolver.resolve(accessor);
		if (userOpt.isEmpty()) {
			subscriptionTracker.drain(sessionId);
			return;
		}
		UserPrincipal user = userOpt.get();
		for (UUID taskId : subscriptionTracker.drain(sessionId)) {
			try {
				taskPresenceService.leave(taskId, user.getId());
			} catch (RuntimeException ex) {
				log.warn("leave task presence on websocket disconnect failed taskId={} userId={}", taskId, user.getId(), ex);
			}
		}
		try {
			userOnlineStatusService.markOffline(user.getId());
		} catch (RuntimeException ex) {
			log.error("mark user offline on websocket disconnect failed userId={}", user.getId(), ex);
		}
	}
}
