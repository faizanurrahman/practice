package com.practice.todo.modules.task.infrastructure.persistence;

import com.practice.todo.modules.task.application.port.SubtaskProposalRepositoryPort;
import com.practice.todo.modules.task.domain.ProposalStatus;
import com.practice.todo.modules.task.domain.SubtaskProposal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubtaskProposalJpaRepository
		extends JpaRepository<SubtaskProposal, UUID>, SubtaskProposalRepositoryPort {

	@Query("select p from SubtaskProposal p where p.parentTask.id = :parentId and p.status = :status order by p.proposedAt asc")
	List<SubtaskProposal> findByParentTaskIdAndStatus(
			@Param("parentId") UUID parentId, @Param("status") ProposalStatus status);

	@Query("select p from SubtaskProposal p join fetch p.parentTask where p.id = :id")
	Optional<SubtaskProposal> findByIdWithParent(@Param("id") UUID id);

	Optional<SubtaskProposal> findByIdAndProposerId(UUID id, UUID proposerId);
}
