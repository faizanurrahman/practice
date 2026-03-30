package com.practice.todo.modules.eventing;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectionEventRepository extends JpaRepository<ProjectionEvent, UUID> {

	boolean existsByProjectionAndEventId(String projection, UUID eventId);
}
