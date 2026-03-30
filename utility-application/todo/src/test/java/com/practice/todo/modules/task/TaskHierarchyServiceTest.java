package com.practice.todo.modules.task;

import com.practice.todo.modules.task.domain.Task;
import com.practice.todo.modules.task.domain.TaskHierarchyService;
import com.practice.todo.modules.task.domain.TaskPathCodec;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskHierarchyServiceTest {

	@Test
	void rewriteSubtreePathsUpdatesDepths() {
		Task root = new Task();
		root.setId(UUID.randomUUID());
		root.setPath("0000000001");
		root.setDepth(0);

		Task child = new Task();
		child.setId(UUID.randomUUID());
		child.setPath("0000000001.0000000002");
		child.setDepth(1);

		Task grand = new Task();
		grand.setId(UUID.randomUUID());
		grand.setPath("0000000001.0000000002.0000000003");
		grand.setDepth(2);

		TaskHierarchyService service = new TaskHierarchyService();
		service.rewriteSubtreePaths(root, "0000000009", List.of(root, child, grand));

		assertEquals("0000000009", root.getPath());
		assertEquals(TaskPathCodec.depthFromPath(root.getPath()), root.getDepth());
		assertEquals("0000000009.0000000002", child.getPath());
		assertEquals(TaskPathCodec.depthFromPath(child.getPath()), child.getDepth());
		assertEquals("0000000009.0000000002.0000000003", grand.getPath());
		assertEquals(TaskPathCodec.depthFromPath(grand.getPath()), grand.getDepth());
	}

	@Test
	void isDescendantPathUsesDotBoundary() {
		TaskHierarchyService service = new TaskHierarchyService();
		assertTrue(service.isDescendantPath("0000000001", "0000000001.0000000002"));
		assertTrue(service.isDescendantPath("0000000001", "0000000001"));
		assertFalse(service.isDescendantPath("0000000001", "00000000011"));
	}
}
