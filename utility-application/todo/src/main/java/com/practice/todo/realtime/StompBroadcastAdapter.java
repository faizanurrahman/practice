package com.practice.todo.realtime;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Primary
@RequiredArgsConstructor
public class StompBroadcastAdapter implements RealTimeBroadcastPort {

	private final SimpMessagingTemplate messagingTemplate;

	@Override
	public void broadcast(String destination, Object payload) {
		messagingTemplate.convertAndSend("/" + destination, payload);
	}

	@Override
	public void broadcastToTask(UUID taskId, String channel, Object payload) {
		messagingTemplate.convertAndSend(taskDestination(taskId, channel), payload);
	}

	public static String taskDestination(UUID taskId, String channel) {
		return "/topic/tasks/" + taskId + "/" + channel;
	}

	public static String taskDestinationSegment(UUID taskId, String channel) {
		return "topic/tasks/" + taskId + "/" + channel;
	}
}
