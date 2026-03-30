package com.practice.todo.modules.iam;

import java.util.UUID;

public interface ProjectAccessPolicy {

	boolean canAccessProject(UUID userId, UUID projectId);

	boolean canModifyProject(UUID userId, UUID projectId);
}
