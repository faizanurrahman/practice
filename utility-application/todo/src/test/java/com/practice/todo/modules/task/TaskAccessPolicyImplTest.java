package com.practice.todo.modules.task;

import com.practice.todo.modules.iam.WorkspaceAccessPolicy;
import com.practice.todo.modules.task.application.policy.TaskAccessPolicyImpl;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskAccessPolicyImplTest {

	@Test
	void allowsOwnerOrAdminOrAssignee() {
		UUID userId = UUID.randomUUID();
		UUID workspaceId = UUID.randomUUID();
		WorkspaceAccessPolicy ownerPolicy = new TestWorkspaceAccessPolicy(true, false);
		TaskAccessPolicyImpl ownerAccess = new TaskAccessPolicyImpl(ownerPolicy);
		assertTrue(ownerAccess.canModifyTask(userId, workspaceId, null));

		WorkspaceAccessPolicy adminPolicy = new TestWorkspaceAccessPolicy(false, true);
		TaskAccessPolicyImpl adminAccess = new TaskAccessPolicyImpl(adminPolicy);
		assertTrue(adminAccess.canModifyTask(userId, workspaceId, null));

		WorkspaceAccessPolicy memberPolicy = new TestWorkspaceAccessPolicy(false, false);
		TaskAccessPolicyImpl assigneeAccess = new TaskAccessPolicyImpl(memberPolicy);
		assertTrue(assigneeAccess.canModifyTask(userId, workspaceId, userId));
		assertFalse(assigneeAccess.canModifyTask(userId, workspaceId, UUID.randomUUID()));
	}

	private record TestWorkspaceAccessPolicy(boolean owner, boolean admin) implements WorkspaceAccessPolicy {
		@Override
		public boolean isMember(UUID userId, UUID workspaceId) {
			return true;
		}

		@Override
		public boolean isAdmin(UUID userId, UUID workspaceId) {
			return admin;
		}

		@Override
		public boolean isOwner(UUID userId, UUID workspaceId) {
			return owner;
		}
	}
}
