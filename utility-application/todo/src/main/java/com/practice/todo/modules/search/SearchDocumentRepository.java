package com.practice.todo.modules.search;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SearchDocumentRepository extends JpaRepository<SearchDocument, UUID> {

	Optional<SearchDocument> findByDocTypeAndRefId(String docType, UUID refId);

	@Query(
			"""
			select d from SearchDocument d
			where lower(d.title) like lower(concat('%', :q, '%'))
			order by d.updatedAt desc
			""")
	List<SearchDocument> searchByTitle(@Param("q") String query);
}
