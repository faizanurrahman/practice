package com.practice.todo.modules.presence.infrastructure.redis;

public final class TodoPresenceRedisKeys {

	public static final String USER_ONLINE = "todo:user:online";

	public static String taskPresenceZset(String taskId) {
		return "todo:task:presence:" + taskId;
	}

	public static String taskPresenceNames(String taskId) {
		return "todo:task:presence:names:" + taskId;
	}

	private TodoPresenceRedisKeys() {}
}
