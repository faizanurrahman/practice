package com.practice.todo.expiry;

import com.practice.todo.project.Project;
import com.practice.todo.task.Task;
import com.practice.todo.task.TaskRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeadlineOwnerResolver {

	private final TaskRepository taskRepository;

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
