package com.practice.todo.realtime;

import java.util.UUID;

public interface RealTimeBroadcastPort {

	void broadcast(String destination, Object payload);

	void broadcastToTask(UUID taskId, String channel, Object payload);
}
