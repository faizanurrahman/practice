package com.practice.todo.modules.content.infrastructure.persistence;

import com.practice.todo.modules.content.domain.TaskBlock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskBlockJpaRepository extends JpaRepository<TaskBlock, UUID> {

	List<TaskBlock> findByTaskIdAndDeletedAtIsNullOrderBySortOrderAsc(UUID taskId);

	Optional<TaskBlock> findByIdAndTaskIdAndDeletedAtIsNull(UUID id, UUID taskId);

	long countByTaskIdAndDeletedAtIsNull(UUID taskId);
}
