package com.practice.todo.modules.task.domain;

import java.util.Locale;

public final class TaskPriorityParser {

	private TaskPriorityParser() {}

	public static String parseToStoredName(String raw) {
		if (raw == null || raw.isBlank()) {
			return "MEDIUM";
		}
		return raw.trim().toUpperCase(Locale.ROOT);
	}
}
