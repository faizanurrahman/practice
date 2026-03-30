package com.practice.todo.modules.search.application.model;

import java.util.List;

public record SearchResults<T>(List<T> items, long totalElements, int page, int size) {}
