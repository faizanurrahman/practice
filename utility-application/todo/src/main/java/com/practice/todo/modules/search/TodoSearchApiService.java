package com.practice.todo.modules.search;

import com.practice.todo.modules.project.domain.Project;
import com.practice.todo.modules.project.infrastructure.persistence.ProjectJpaRepository;
import com.practice.todo.modules.search.application.model.AutocompleteResults;
import com.practice.todo.modules.search.application.model.AutocompleteSuggestion;
import com.practice.todo.modules.search.application.model.SearchResults;
import com.practice.todo.modules.search.application.model.SearchableProject;
import com.practice.todo.modules.search.application.model.SearchableTask;
import com.practice.todo.modules.search.application.model.SearchableUser;
import com.practice.todo.modules.task.domain.Task;
import com.practice.todo.modules.task.domain.TaskLabel;
import com.practice.todo.modules.task.infrastructure.persistence.TaskJpaRepository;
import com.practice.todo.modules.task.infrastructure.persistence.TaskLabelJpaRepository;
import com.practice.todo.modules.user.domain.User;
import com.practice.todo.modules.user.infrastructure.persistence.UserJpaRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TodoSearchApiService {

	private final TaskJpaRepository taskJpaRepository;
	private final ProjectJpaRepository projectJpaRepository;
	private final UserJpaRepository userJpaRepository;
	private final TaskLabelJpaRepository taskLabelJpaRepository;
	private final SearchDocumentRepository searchDocumentRepository;
	private final org.springframework.beans.factory.ObjectProvider<TodoElasticsearchSearchDelegate> elasticsearchDelegate;

	public SearchResults<SearchableTask> searchTasks(UUID userId, String q, int page, int size) {
		var es = elasticsearchDelegate.getIfAvailable();
		if (es != null) {
			return es.searchTasks(userId, q, page, size);
		}
		return searchTasksJpa(userId, q, page, size);
	}

	public SearchResults<SearchableProject> searchProjects(UUID userId, String q, int page, int size) {
		var es = elasticsearchDelegate.getIfAvailable();
		if (es != null) {
			return es.searchProjects(userId, q, page, size);
		}
		return searchProjectsJpa(userId, q, page, size);
	}

	public SearchResults<SearchableUser> searchUsers(UUID userId, String q, int page, int size) {
		var es = elasticsearchDelegate.getIfAvailable();
		if (es != null) {
			return es.searchUsers(userId, q, page, size);
		}
		return searchUsersJpa(userId, q, page, size);
	}

	public List<LegacySearchHit> legacyTitleSearch(String q) {
		if (q == null || q.isBlank()) {
			return List.of();
		}
		return searchDocumentRepository.searchByTitle(q.trim()).stream()
				.map(d -> new LegacySearchHit(d.getDocType(), d.getRefId(), d.getTitle(), d.getWorkspaceId()))
				.toList();
	}

	public AutocompleteResults autocomplete(UUID userId, String q, String type, int limit) {
		if (q == null || q.isBlank()) {
			return new AutocompleteResults(List.of());
		}
		var es = elasticsearchDelegate.getIfAvailable();
		if (es != null) {
			return es.autocomplete(userId, q, type, limit);
		}
		return autocompleteJpa(userId, q, type, limit);
	}

	private SearchResults<SearchableTask> searchTasksJpa(UUID userId, String q, int page, int size) {
		boolean blank = q == null || q.isBlank();
		Pageable p = PageRequest.of(Math.max(0, page), Math.min(100, Math.max(1, size)));
		var result = taskJpaRepository.searchAccessibleForUser(userId, blank ? "" : q.trim(), blank, p);
		List<Task> tasks = result.getContent();
		Map<UUID, List<String>> labelsByTask = loadLabels(tasks.stream().map(Task::getId).toList());
		List<SearchableTask> items = tasks.stream()
				.map(t -> toSearchableTask(t, labelsByTask.getOrDefault(t.getId(), List.of())))
				.toList();
		return new SearchResults<>(items, result.getTotalElements(), page, p.getPageSize());
	}

	private SearchResults<SearchableProject> searchProjectsJpa(UUID userId, String q, int page, int size) {
		boolean blank = q == null || q.isBlank();
		Pageable p = PageRequest.of(Math.max(0, page), Math.min(100, Math.max(1, size)));
		var result = projectJpaRepository.searchAccessibleForUser(userId, blank ? "" : q.trim(), blank, p);
		List<SearchableProject> items = result.getContent().stream()
				.map(TodoSearchApiService::toSearchableProject)
				.toList();
		return new SearchResults<>(items, result.getTotalElements(), page, p.getPageSize());
	}

	private SearchResults<SearchableUser> searchUsersJpa(UUID userId, String q, int page, int size) {
		boolean blank = q == null || q.isBlank();
		Pageable p = PageRequest.of(Math.max(0, page), Math.min(100, Math.max(1, size)));
		var result = userJpaRepository.searchCoworkers(userId, blank ? "" : q.trim(), blank, p);
		List<SearchableUser> items = result.getContent().stream()
				.map(TodoSearchApiService::toSearchableUser)
				.toList();
		return new SearchResults<>(items, result.getTotalElements(), page, p.getPageSize());
	}

	private AutocompleteResults autocompleteJpa(UUID userId, String q, String type, int limit) {
		int cap = Math.min(50, Math.max(1, limit));
		List<AutocompleteSuggestion> out = new ArrayList<>();
		String t = type == null ? "" : type.toLowerCase();
		if (t.isEmpty() || t.equals("task")) {
			searchTasksJpa(userId, q, 0, cap).items().stream()
					.map(x -> new AutocompleteSuggestion("TASK", x.id(), x.title(), x.status()))
					.forEach(out::add);
		}
		if (t.isEmpty() || t.equals("project")) {
			searchProjectsJpa(userId, q, 0, cap).items().stream()
					.map(x -> new AutocompleteSuggestion("PROJECT", x.id(), x.name(), x.workspaceId().toString()))
					.forEach(out::add);
		}
		if (t.isEmpty() || t.equals("user")) {
			searchUsersJpa(userId, q, 0, cap).items().stream()
					.map(x -> new AutocompleteSuggestion("USER", x.id(), x.email(), x.displayName()))
					.forEach(out::add);
		}
		return new AutocompleteResults(out.stream().limit(cap).toList());
	}

	private Map<UUID, List<String>> loadLabels(List<UUID> taskIds) {
		if (taskIds.isEmpty()) {
			return Map.of();
		}
		List<TaskLabel> links = taskLabelJpaRepository.findByTaskIdIn(taskIds);
		Map<UUID, List<String>> map = new HashMap<>();
		for (TaskLabel tl : links) {
			map.computeIfAbsent(tl.getTaskId(), k -> new ArrayList<>())
					.add(tl.getLabel().getName());
		}
		return map;
	}

	private static SearchableTask toSearchableTask(Task t, List<String> labels) {
		List<UUID> members = t.getAssigneeUserId() != null ? List.of(t.getAssigneeUserId()) : List.of();
		return new SearchableTask(
				t.getId(),
				t.getProject().getId(),
				t.getTitle(),
				t.getStatus().name(),
				"",
				labels,
				members,
				t.getCreatedAt());
	}

	private static SearchableProject toSearchableProject(Project p) {
		return new SearchableProject(
				p.getId(),
				p.getWorkspace().getId(),
				p.getName(),
				p.getDescription(),
				p.getVisibility().name(),
				p.getUpdatedAt());
	}

	private static SearchableUser toSearchableUser(User u) {
		return new SearchableUser(u.getId(), u.getEmail(), u.getEmail());
	}

	public record LegacySearchHit(String docType, java.util.UUID refId, String title, java.util.UUID workspaceId) {}
}
