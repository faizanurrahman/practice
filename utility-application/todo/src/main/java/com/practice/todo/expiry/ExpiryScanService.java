package com.practice.todo.expiry;

import com.practice.todo.project.Project;
import com.practice.todo.project.ProjectRepository;
import com.practice.todo.task.Task;
import com.practice.todo.task.TaskRepository;
import com.practice.todo.task.TaskStatus;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExpiryScanService {

	private final TaskRepository taskRepository;
	private final ProjectRepository projectRepository;
	private final DeadlineOwnerResolver deadlineOwnerResolver;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public void scanAndPublish(Instant now) {
		publishTaskDueEvents(now);
		publishProjectTimelineEvents(now);
	}

	private void publishTaskDueEvents(Instant now) {
		List<Task> overdue = taskRepository.findOverdueExplicitDue(now, TaskStatus.DONE);
		for (Task t : overdue) {
			var owner = t.getProject().getOwner();
			String dedupe = "task:" + t.getId() + ":due:" + t.getDueAt().toEpochMilli();
			eventPublisher.publishEvent(new TaskDueExpiredEvent(
					owner.getId(), t.getId(), t.getTitle(), t.getDueAt(), dedupe));
		}
	}

	private void publishProjectTimelineEvents(Instant now) {
		List<Project> projects = projectRepository.findWithPastTimeline(now);
		for (Project p : projects) {
			Instant end = p.getTimelineEndAt();
			if (end == null || !end.isBefore(now)) {
				continue;
			}
			List<Task> tasks = taskRepository.findAllActiveByProjectId(p.getId());
			boolean notify = tasks.stream()
					.filter(t -> t.getStatus() != TaskStatus.DONE)
					.anyMatch(t -> deadlineOwnerResolver
							.notificationOwner(t)
							.map(o -> o.kind() == DeadlineOwner.Kind.PROJECT && o.id().equals(p.getId()))
							.orElse(false));
			if (notify) {
				String dedupe = "project:" + p.getId() + ":timeline:" + end.toEpochMilli();
				eventPublisher.publishEvent(new ProjectTimelineExpiredEvent(
						p.getOwner().getId(), p.getId(), p.getName(), end, dedupe));
			}
		}
	}
}
