package com.practice.todo.modules.eventing;

public record IntegrationEvent(String type, int version, String source, Object payload) {}
