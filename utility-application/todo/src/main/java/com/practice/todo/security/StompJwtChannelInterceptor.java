package com.practice.todo.security;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompJwtChannelInterceptor implements ChannelInterceptor {

	private final JwtService jwtService;
	private final CustomUserDetailsService userDetailsService;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
			return message;
		}
		List<String> headers = accessor.getNativeHeader("Authorization");
		if (headers == null || headers.isEmpty()) {
			return message;
		}
		String raw = headers.getFirst();
		if (raw == null || !raw.startsWith("Bearer ")) {
			return message;
		}
		String token = raw.substring(7);
		if (!jwtService.isValid(token)) {
			return message;
		}
		try {
			UUID userId = jwtService.parseUserId(token);
			UserDetails user = userDetailsService.loadUserById(userId);
			var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
			accessor.setUser(auth);
		} catch (Exception ignored) {
			// leave unauthenticated
		}
		return message;
	}
}
