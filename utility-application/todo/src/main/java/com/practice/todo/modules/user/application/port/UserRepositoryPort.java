package com.practice.todo.modules.user.application.port;

import com.practice.todo.modules.user.domain.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {

	Optional<User> findById(UUID id);

	Optional<User> findByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCase(String email);

	User save(User user);
}
