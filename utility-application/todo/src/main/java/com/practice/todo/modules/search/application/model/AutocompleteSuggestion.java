package com.practice.todo.modules.search.application.model;

import java.util.UUID;

public record AutocompleteSuggestion(String type, UUID id, String label, String subtitle) {}
