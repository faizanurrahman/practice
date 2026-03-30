package com.practice.todo.modules.presence.infrastructure.websocket;

import com.practice.todo.security.UserPrincipal;
import java.security.Principal;
import java.util.Optional;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class StompUserPrincipalResolver {

	public Optional<UserPrincipal> resolve(StompHeaderAccessor accessor) {
		if (accessor == null) {
			return Optional.empty();
		}
		Principal p = accessor.getUser();
		if (p instanceof Authentication auth && auth.getPrincipal() instanceof UserPrincipal up) {
			return Optional.of(up);
		}
		return Optional.empty();
	}
}
