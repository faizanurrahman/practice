package com.practice.todo.modules.project.infrastructure.persistence;

import com.practice.todo.modules.project.application.port.ProjectRepositoryPort;
import com.practice.todo.modules.project.domain.Project;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectJpaRepository extends JpaRepository<Project, UUID>, ProjectRepositoryPort {

	List<Project> findByWorkspaceIdInAndArchivedAtIsNullOrderByCreatedAtDesc(Collection<UUID> workspaceIds);

	Optional<Project> findByIdAndWorkspaceIdIn(UUID id, Collection<UUID> workspaceIds);

	@Query("""
			select distinct p from Project p
			where p.timelineEndAt is not null and p.timelineEndAt < :now
			""")
	List<Project> findWithPastTimeline(@Param("now") Instant now);

	@Query("""
			select distinct p from Project p
			join com.practice.todo.modules.workspace.domain.WorkspaceMember m
			  on m.workspaceId = p.workspace.id and m.userId = :userId
			where p.archivedAt is null
			  and (:blankQuery = true
			    or lower(p.name) like lower(concat('%', :q, '%'))
			    or lower(coalesce(p.description, '')) like lower(concat('%', :q, '%')))
			order by p.updatedAt desc
			""")
	Page<Project> searchAccessibleForUser(
			@Param("userId") UUID userId,
			@Param("q") String q,
			@Param("blankQuery") boolean blankQuery,
			Pageable pageable);
}
