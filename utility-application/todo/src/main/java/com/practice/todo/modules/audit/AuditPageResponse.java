package com.practice.todo.modules.audit;

import java.util.List;

public record AuditPageResponse<T>(List<T> items, int page, int size, long totalElements) {}
