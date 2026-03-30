package com.practice.todo.modules.expiry;

import com.practice.todo.modules.project.application.port.ProjectRepositoryPort;
import com.practice.todo.modules.project.domain.Project;
import com.practice.todo.modules.task.application.port.TaskRepositoryPort;
import com.practice.todo.modules.task.domain.Task;
import com.practice.todo.modules.task.domain.TaskStatus;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExpiryScanService {

	private final TaskRepositoryPort taskRepository;
	private final ProjectRepositoryPort projectRepository;
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
			var owner = t.getProject().getWorkspace().getCreatedBy();
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
						p.getWorkspace().getCreatedBy().getId(), p.getId(), p.getName(), end, dedupe));
			}
		}
	}
}
