package com.practice.todo.modules.task.domain;

public final class TaskPathCodec {

	private TaskPathCodec() {}

	public static String segment(int value) {
		return String.format("%010d", value);
	}

	public static int depthFromPath(String path) {
		if (path == null || path.isEmpty()) {
			return 0;
		}
		return path.split("\\.", -1).length - 1;
	}
}
