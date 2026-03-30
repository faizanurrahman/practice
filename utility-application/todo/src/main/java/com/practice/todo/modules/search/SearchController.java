package com.practice.todo.modules.search;

import com.practice.todo.modules.search.api.SearchApiResponse;
import com.practice.todo.modules.search.application.model.AutocompleteResults;
import com.practice.todo.modules.search.application.model.SearchResults;
import com.practice.todo.modules.search.application.model.SearchableProject;
import com.practice.todo.modules.search.application.model.SearchableTask;
import com.practice.todo.modules.search.application.model.SearchableUser;
import com.practice.todo.security.UserPrincipal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

	private final TodoSearchApiService searchApiService;

	@GetMapping("/tasks")
	public SearchApiResponse<SearchResults<SearchableTask>> searchTasks(
			@AuthenticationPrincipal UserPrincipal user,
			@RequestParam(name = "q", defaultValue = "") String q,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "20") int size) {
		return new SearchApiResponse<>(searchApiService.searchTasks(user.getId(), q, page, size));
	}

	@GetMapping("/projects")
	public SearchApiResponse<SearchResults<SearchableProject>> searchProjects(
			@AuthenticationPrincipal UserPrincipal user,
			@RequestParam(name = "q", defaultValue = "") String q,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "20") int size) {
		return new SearchApiResponse<>(searchApiService.searchProjects(user.getId(), q, page, size));
	}

	@GetMapping("/users")
	public SearchApiResponse<SearchResults<SearchableUser>> searchUsers(
			@AuthenticationPrincipal UserPrincipal user,
			@RequestParam(name = "q", defaultValue = "") String q,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "20") int size) {
		return new SearchApiResponse<>(searchApiService.searchUsers(user.getId(), q, page, size));
	}

	@GetMapping("/autocomplete")
	public SearchApiResponse<AutocompleteResults> autocomplete(
			@AuthenticationPrincipal UserPrincipal user,
			@RequestParam(name = "q") String q,
			@RequestParam(name = "type", required = false) String type,
			@RequestParam(name = "limit", defaultValue = "10") int limit) {
		return new SearchApiResponse<>(searchApiService.autocomplete(user.getId(), q, type, limit));
	}

	/** Backward-compatible unified title search on {@code search_documents}. */
	@GetMapping
	public SearchApiResponse<List<TodoSearchApiService.LegacySearchHit>> legacyTitleSearch(
			@AuthenticationPrincipal UserPrincipal user, @RequestParam("q") String q) {
		return new SearchApiResponse<>(searchApiService.legacyTitleSearch(q));
	}
}
