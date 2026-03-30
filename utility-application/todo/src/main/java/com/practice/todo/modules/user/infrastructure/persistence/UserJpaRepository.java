package com.practice.todo.modules.user.infrastructure.persistence;

import com.practice.todo.modules.user.application.port.UserRepositoryPort;
import com.practice.todo.modules.user.domain.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<User, UUID>, UserRepositoryPort {

	Optional<User> findByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCase(String email);

	@Query("""
			select distinct u from User u
			join com.practice.todo.modules.workspace.domain.WorkspaceMember m1 on m1.userId = u.id
			join com.practice.todo.modules.workspace.domain.WorkspaceMember m2
			  on m2.workspaceId = m1.workspaceId and m2.userId = :userId
			where (:blankQuery = true or lower(u.email) like lower(concat('%', :q, '%')))
			order by u.email asc
			""")
	Page<User> searchCoworkers(
			@Param("userId") UUID userId,
			@Param("q") String q,
			@Param("blankQuery") boolean blankQuery,
			Pageable pageable);
}
