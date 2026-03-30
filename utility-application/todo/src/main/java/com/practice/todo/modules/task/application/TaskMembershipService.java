package com.practice.todo.modules.task.application;

import com.practice.todo.modules.task.domain.Task;
import com.practice.todo.modules.task.domain.TaskMember;
import com.practice.todo.modules.task.domain.TaskMemberRole;
import com.practice.todo.modules.task.application.port.TaskMemberRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskMembershipService {

	private final TaskMemberRepositoryPort taskMemberRepository;

	@Transactional
	public void onTaskCreated(Task task, UUID creatorUserId) {
		addOwnerIfAbsent(task, creatorUserId);
		if (task.getAssigneeUserId() != null
				&& !task.getAssigneeUserId().equals(creatorUserId)) {
			addMemberIfAbsent(task, task.getAssigneeUserId());
		}
	}

	@Transactional
	public void addOwnerIfAbsent(Task task, UUID userId) {
		if (taskMemberRepository.existsByTaskIdAndUserIdAndRole(
				task.getId(), userId, TaskMemberRole.OWNER)) {
			return;
		}
		TaskMember owner = new TaskMember();
		owner.setTask(task);
		owner.setUserId(userId);
		owner.setRole(TaskMemberRole.OWNER);
		taskMemberRepository.save(owner);
	}

	@Transactional
	public void syncAssignee(Task task, UUID previousAssignee, UUID newAssignee) {
		if (previousAssignee != null
				&& !previousAssignee.equals(newAssignee)
				&& !taskMemberRepository.existsByTaskIdAndUserIdAndRole(
						task.getId(), previousAssignee, TaskMemberRole.OWNER)) {
			taskMemberRepository.deleteByTask_IdAndUserId(task.getId(), previousAssignee);
		}
		if (newAssignee != null) {
			addMemberIfAbsent(task, newAssignee);
		}
	}

	@Transactional
	public void ensureMemberForTask(Task task, UUID userId) {
		addMemberIfAbsent(task, userId);
	}

	private void addMemberIfAbsent(Task task, UUID userId) {
		if (taskMemberRepository.existsByTaskIdAndUserIdAndRole(
				task.getId(), userId, TaskMemberRole.OWNER)) {
			return;
		}
		if (taskMemberRepository.existsByTaskIdAndUserIdAndRole(
				task.getId(), userId, TaskMemberRole.MEMBER)) {
			return;
		}
		TaskMember m = new TaskMember();
		m.setTask(task);
		m.setUserId(userId);
		m.setRole(TaskMemberRole.MEMBER);
		taskMemberRepository.save(m);
	}
}
