package com.practice.todo.modules.workspace.infrastructure.persistence;

import com.practice.todo.modules.workspace.application.port.WorkspaceRepositoryPort;
import com.practice.todo.modules.workspace.domain.Workspace;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceJpaRepository extends JpaRepository<Workspace, UUID>, WorkspaceRepositoryPort {}
