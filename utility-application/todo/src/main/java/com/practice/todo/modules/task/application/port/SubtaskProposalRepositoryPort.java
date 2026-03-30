package com.practice.todo.modules.task.application.port;

import com.practice.todo.modules.task.domain.ProposalStatus;
import com.practice.todo.modules.task.domain.SubtaskProposal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubtaskProposalRepositoryPort {

	List<SubtaskProposal> findByParentTaskIdAndStatus(UUID parentId, ProposalStatus status);

	Optional<SubtaskProposal> findByIdWithParent(UUID id);

	SubtaskProposal save(SubtaskProposal proposal);
}
