package com.practice.todo.modules.jobs;

public record JobRequest(String name, int version, Object payload) {}
