package com.practice.todo.modules.presence.application;

import com.practice.todo.modules.presence.domain.TaskPresence;
import com.practice.todo.modules.project.application.port.ProjectAccessPort;
import com.practice.todo.modules.task.application.port.TaskRepositoryPort;
import com.practice.todo.realtime.RealTimeBroadcastPort;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskPresenceService {

	private final TaskPresencePort taskPresencePort;
	private final RealTimeBroadcastPort realTimeBroadcastPort;
	private final TaskRepositoryPort taskRepository;
	private final ProjectAccessPort projectAccessPort;

	public void join(UUID taskId, UUID userId, String displayName) {
		Objects.requireNonNull(taskId, "taskId");
		Objects.requireNonNull(userId, "userId");
		assertCanViewTask(userId, taskId);
		String name = displayName != null && !displayName.isBlank() ? displayName : "Anonymous";
		taskPresencePort.join(taskId, userId, name);
		broadcastSnapshot(taskId);
	}

	public void leave(UUID taskId, UUID userId) {
		Objects.requireNonNull(taskId, "taskId");
		Objects.requireNonNull(userId, "userId");
		taskPresencePort.leave(taskId, userId);
		broadcastSnapshot(taskId);
	}

	public void heartbeat(UUID taskId, UUID userId) {
		Objects.requireNonNull(taskId, "taskId");
		Objects.requireNonNull(userId, "userId");
		assertCanViewTask(userId, taskId);
		taskPresencePort.heartbeat(taskId, userId);
	}

	public Set<TaskPresence> getActiveUsers(UUID taskId) {
		return taskPresencePort.getActiveUsers(taskId);
	}

	private void assertCanViewTask(UUID userId, UUID taskId) {
		var task = taskRepository
				.findByIdWithProject(taskId)
				.filter(t -> !t.isDeleted())
				.orElseThrow(() -> new IllegalArgumentException("Task not found"));
		projectAccessPort.getAccessibleProject(userId, task.getProject().getId());
	}

	public void evictStaleMembers(long maxIdleMillis) {
		if (maxIdleMillis <= 0) {
			log.warn("evictStaleMembers ignored: maxIdleMillis must be positive");
			return;
		}
		taskPresencePort.evictStaleMembers(maxIdleMillis);
	}

	private void broadcastSnapshot(UUID taskId) {
		Set<TaskPresence> active = taskPresencePort.getActiveUsers(taskId);
		realTimeBroadcastPort.broadcastToTask(taskId, "presence", new TaskPresenceSnapshot(taskId, active));
	}

	public record TaskPresenceSnapshot(UUID taskId, Set<TaskPresence> activeUsers) {}
}
