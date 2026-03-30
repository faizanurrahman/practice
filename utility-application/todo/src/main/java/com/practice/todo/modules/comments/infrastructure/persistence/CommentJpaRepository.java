package com.practice.todo.modules.comments.infrastructure.persistence;

import com.practice.todo.modules.comments.domain.Comment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<Comment, UUID> {

	List<Comment> findByBlockIdAndDeletedAtIsNullOrderByPathAsc(UUID blockId);

	List<Comment> findByTaskIdAndDeletedAtIsNullOrderByPathAsc(UUID taskId);

	long countByBlockIdAndDeletedAtIsNull(UUID blockId);

	long countByBlockIdAndDeletedAtIsNullAndParentCommentIdIsNull(UUID blockId);

	long countByBlockIdAndDeletedAtIsNullAndParentCommentId(UUID blockId, UUID parentCommentId);

	Optional<Comment> findByIdAndTaskIdAndDeletedAtIsNull(UUID id, UUID taskId);
}
