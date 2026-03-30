package com.practice.todo.modules.jobs;

public enum JobMode {
	JOBRUNR,
	REDIS;

	public static JobMode from(String value) {
		if (value == null || value.isBlank()) {
			return JOBRUNR;
		}
		try {
			return JobMode.valueOf(value.trim().toUpperCase());
		} catch (IllegalArgumentException ex) {
			return JOBRUNR;
		}
	}
}
