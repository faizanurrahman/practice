package com.practice.todo.modules.task.domain;

import java.util.List;

public class TaskHierarchyService {

	public String computePathForNewTask(Task parent, int sortOrder) {
		if (parent == null) {
			return TaskPathCodec.segment(sortOrder);
		}
		return parent.getPath() + "." + TaskPathCodec.segment(sortOrder);
	}

	public boolean isDescendantPath(String ancestorPath, String candidatePath) {
		if (ancestorPath == null || candidatePath == null) {
			return false;
		}
		return candidatePath.equals(ancestorPath) || candidatePath.startsWith(ancestorPath + ".");
	}

	public void rewriteSubtreePaths(Task root, String newRootPath, List<Task> subtree) {
		String oldRootPath = root.getPath();
		for (Task node : subtree) {
			if (node.getId().equals(root.getId())) {
				node.setPath(newRootPath);
				node.setDepth(TaskPathCodec.depthFromPath(newRootPath));
			} else {
				String rest = node.getPath().substring(oldRootPath.length() + 1);
				String nextPath = newRootPath + "." + rest;
				node.setPath(nextPath);
				node.setDepth(TaskPathCodec.depthFromPath(nextPath));
			}
		}
	}
}
