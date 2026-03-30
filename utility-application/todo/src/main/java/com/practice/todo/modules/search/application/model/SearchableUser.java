package com.practice.todo.modules.search.application.model;

import java.util.UUID;

public record SearchableUser(UUID id, String email, String displayName) {}
