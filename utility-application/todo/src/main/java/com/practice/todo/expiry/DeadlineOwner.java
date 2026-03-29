package com.practice.todo.expiry;

import java.util.UUID;

public record DeadlineOwner(Kind kind, UUID id) {

	public enum Kind {
		TASK,
		PROJECT
	}

	public static DeadlineOwner task(UUID id) {
		return new DeadlineOwner(Kind.TASK, id);
	}

	public static DeadlineOwner project(UUID id) {
		return new DeadlineOwner(Kind.PROJECT, id);
	}
}
