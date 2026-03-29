package com.practice.todo.expiry;

import com.practice.todo.notification.AppNotification;
import com.practice.todo.notification.AppNotificationRepository;
import com.practice.todo.notification.NotificationType;
import com.practice.todo.user.User;
import com.practice.todo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ExpiryNotificationListener {

	private final AppNotificationRepository notificationRepository;
	private final UserRepository userRepository;

	@EventListener
	@Transactional
	public void onTaskDue(TaskDueExpiredEvent e) {
		if (notificationRepository.existsByUserIdAndDedupeKey(e.userId(), e.dedupeKey())) {
			return;
		}
		User u = userRepository.getReferenceById(e.userId());
		AppNotification n = new AppNotification();
		n.setUser(u);
		n.setType(NotificationType.TASK_DUE_EXPIRED);
		n.setTitle("Task deadline passed");
		n.setBody("Task \"" + e.taskTitle() + "\" was due at " + e.dueAt());
		n.setDedupeKey(e.dedupeKey());
		notificationRepository.save(n);
	}

	@EventListener
	@Transactional
	public void onProjectTimeline(ProjectTimelineExpiredEvent e) {
		if (notificationRepository.existsByUserIdAndDedupeKey(e.userId(), e.dedupeKey())) {
			return;
		}
		User u = userRepository.getReferenceById(e.userId());
		AppNotification n = new AppNotification();
		n.setUser(u);
		n.setType(NotificationType.PROJECT_TIMELINE_EXPIRED);
		n.setTitle("Project timeline ended");
		n.setBody("Project \"" + e.projectName() + "\" timeline ended at " + e.timelineEndAt());
		n.setDedupeKey(e.dedupeKey());
		notificationRepository.save(n);
	}
}
