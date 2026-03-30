package com.practice.todo.modules.search;

import com.practice.todo.modules.search.application.model.AutocompleteResults;
import com.practice.todo.modules.search.application.model.SearchResults;
import com.practice.todo.modules.search.application.model.SearchableProject;
import com.practice.todo.modules.search.application.model.SearchableTask;
import com.practice.todo.modules.search.application.model.SearchableUser;
import java.util.UUID;

/**
 * Optional bean: register an implementation (e.g. under {@code infrastructure.elastic}) when
 * {@code spring.elasticsearch.uris} is set to route search/autocomplete through Elasticsearch instead of JPA.
 */
public interface TodoElasticsearchSearchDelegate {

	SearchResults<SearchableTask> searchTasks(UUID userId, String q, int page, int size);

	SearchResults<SearchableProject> searchProjects(UUID userId, String q, int page, int size);

	SearchResults<SearchableUser> searchUsers(UUID userId, String q, int page, int size);

	AutocompleteResults autocomplete(UUID userId, String q, String type, int limit);
}
