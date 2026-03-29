package com.practice.todo.project;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

	List<Project> findByOwnerIdOrderByCreatedAtDesc(UUID ownerId);

	Optional<Project> findByIdAndOwnerId(UUID id, UUID ownerId);

	@Query("""
			select distinct p from Project p
			left join fetch p.owner
			where p.timelineEndAt is not null and p.timelineEndAt < :now
			""")
	List<Project> findWithPastTimeline(@Param("now") Instant now);
}
