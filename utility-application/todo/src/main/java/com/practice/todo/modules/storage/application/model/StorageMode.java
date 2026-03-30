package com.practice.todo.modules.storage.application.model;

public enum StorageMode {
	LOCAL,
	MINIO,
	S3;

	public static StorageMode from(String value) {
		if (value == null || value.isBlank()) {
			return LOCAL;
		}
		try {
			return StorageMode.valueOf(value.trim().toUpperCase());
		} catch (IllegalArgumentException ex) {
			return LOCAL;
		}
	}
}
