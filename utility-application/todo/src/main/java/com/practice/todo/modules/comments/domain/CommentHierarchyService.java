package com.practice.todo.modules.comments.domain;

import org.springframework.stereotype.Component;

@Component
public class CommentHierarchyService {

	public String computePathForNewComment(Comment parent, int sortOrder) {
		String segment = segment(sortOrder);
		if (parent == null || parent.getPath() == null || parent.getPath().isEmpty()) {
			return segment;
		}
		return parent.getPath() + "." + segment;
	}

	public int depthForPath(String path) {
		if (path == null || path.isEmpty()) {
			return 0;
		}
		int dots = 0;
		for (int i = 0; i < path.length(); i++) {
			if (path.charAt(i) == '.') {
				dots++;
			}
		}
		return dots + 1;
	}

	private static String segment(int order) {
		return String.format("%04d", order);
	}
}
