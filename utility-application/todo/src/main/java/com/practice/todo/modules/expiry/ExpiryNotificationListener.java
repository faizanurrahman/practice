package com.practice.todo.modules.expiry;

import com.practice.todo.modules.jobs.JobQueuePort;
import com.practice.todo.modules.jobs.JobRequest;
import com.practice.todo.modules.notification.NotificationType;
import com.practice.todo.modules.notification.channel.NotificationChannel;
import com.practice.todo.modules.notification.job.NotificationDispatchPayload;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ExpiryNotificationListener {

	private final JobQueuePort jobQueuePort;

	@EventListener
	@Transactional
	public void onTaskDue(TaskDueExpiredEvent e) {
		NotificationDispatchPayload payload = new NotificationDispatchPayload(
				e.userId(),
				NotificationType.TASK_DUE_EXPIRED,
				"Task deadline passed",
				"Task \"" + e.taskTitle() + "\" was due at " + e.dueAt(),
				e.dedupeKey(),
				List.of(NotificationChannel.IN_APP, NotificationChannel.EMAIL));
		jobQueuePort.enqueue(new JobRequest("notification.send", 1, payload));
	}

	@EventListener
	@Transactional
	public void onProjectTimeline(ProjectTimelineExpiredEvent e) {
		NotificationDispatchPayload payload = new NotificationDispatchPayload(
				e.userId(),
				NotificationType.PROJECT_TIMELINE_EXPIRED,
				"Project timeline ended",
				"Project \"" + e.projectName() + "\" timeline ended at " + e.timelineEndAt(),
				e.dedupeKey(),
				List.of(NotificationChannel.IN_APP, NotificationChannel.EMAIL));
		jobQueuePort.enqueue(new JobRequest("notification.send", 1, payload));
	}
}
