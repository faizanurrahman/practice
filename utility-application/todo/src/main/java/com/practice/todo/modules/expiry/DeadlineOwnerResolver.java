package com.practice.todo.modules.expiry;

import com.practice.todo.modules.project.domain.Project;
import com.practice.todo.modules.task.application.port.TaskRepositoryPort;
import com.practice.todo.modules.task.domain.Task;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeadlineOwnerResolver {

	private final TaskRepositoryPort taskRepository;

	/**
	 * Deadline owner for notifications: first explicit {@code dueAt} walking up from {@code t},
	 * else project timeline if set.
	 */
	public Optional<DeadlineOwner> notificationOwner(Task t) {
		Task cur = t;
		while (cur != null) {
			if (cur.getDueAt() != null) {
				return Optional.of(DeadlineOwner.task(cur.getId()));
			}
			if (cur.getParent() == null) {
				break;
			}
			UUID pid = cur.getParent().getId();
			cur = taskRepository.findByIdAndDeletedAtIsNull(pid).orElse(null);
		}
		Project p = t.getProject();
		if (p.getTimelineEndAt() != null) {
			return Optional.of(DeadlineOwner.project(p.getId()));
		}
		return Optional.empty();
	}
}
