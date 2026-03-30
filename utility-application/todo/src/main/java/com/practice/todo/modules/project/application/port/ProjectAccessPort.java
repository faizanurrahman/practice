package com.practice.todo.modules.project.application.port;

import com.practice.todo.modules.project.domain.Project;
import java.util.UUID;

public interface ProjectAccessPort {

	Project getAccessibleProject(UUID userId, UUID projectId);
}
