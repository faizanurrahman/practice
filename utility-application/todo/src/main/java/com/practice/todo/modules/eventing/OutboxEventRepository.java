package com.practice.todo.modules.eventing;

import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			select e from OutboxEvent e
			where e.publishedAt is null
			  and (e.nextAttemptAt is null or e.nextAttemptAt <= :now)
			order by e.createdAt asc
			""")
	List<OutboxEvent> findBatchForPublish(@Param("now") Instant now, Pageable pageable);
}
