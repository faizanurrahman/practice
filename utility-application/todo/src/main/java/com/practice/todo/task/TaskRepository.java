package com.practice.todo.task;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, UUID> {

	@Query("""
			select t from Task t
			where t.project.id = :projectId and t.parent is null and t.deletedAt is null
			order by t.sortOrder asc, t.createdAt asc
			""")
	List<Task> findRootsByProjectId(@Param("projectId") UUID projectId);

	@Query("""
			select t from Task t
			where t.parent.id = :parentId and t.deletedAt is null
			order by t.sortOrder asc, t.createdAt asc
			""")
	List<Task> findChildren(@Param("parentId") UUID parentId);

	Optional<Task> findByIdAndDeletedAtIsNull(UUID id);

	@Query("""
			select t from Task t join fetch t.project p
			where t.id = :id and t.deletedAt is null
			""")
	Optional<Task> findByIdWithProject(@Param("id") UUID id);

	@Query("""
			select t.id from Task t
			where t.parent.id = :parentId and t.deletedAt is null
			""")
	List<UUID> findChildIds(@Param("parentId") UUID parentId);

	@Query("""
			select count(t) from Task t
			where t.parent.id = :parentId and t.deletedAt is null and t.status <> :done
			""")
	long countActiveIncompleteChildren(@Param("parentId") UUID parentId, @Param("done") TaskStatus done);

	@Query("""
			select t from Task t
			where t.project.id = :projectId and t.deletedAt is null
			""")
	List<Task> findAllActiveByProjectId(@Param("projectId") UUID projectId);

	@Query("""
			select t from Task t
			where t.project.id = :projectId and t.deletedAt is null and t.dueAt is not null
			""")
	List<Task> findActiveWithExplicitDueByProjectId(@Param("projectId") UUID projectId);

	@Query("""
			select t from Task t join fetch t.project p join fetch p.owner
			where t.id in :ids and t.deletedAt is null
			""")
	List<Task> findAllByIdInWithProject(@Param("ids") Collection<UUID> ids);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update Task t set t.deletedAt = :at where t.id in :ids")
	void markDeleted(@Param("ids") Collection<UUID> ids, @Param("at") Instant at);

	@Query("""
			select t from Task t join fetch t.project p join fetch p.owner
			where t.deletedAt is null and t.status <> :done
			  and t.dueAt is not null and t.dueAt < :now
			""")
	List<Task> findOverdueExplicitDue(@Param("now") Instant now, @Param("done") TaskStatus done);
}
