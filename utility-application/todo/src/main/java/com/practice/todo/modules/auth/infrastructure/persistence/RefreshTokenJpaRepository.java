package com.practice.todo.modules.auth.infrastructure.persistence;

import com.practice.todo.modules.auth.application.port.RefreshTokenRepositoryPort;
import com.practice.todo.modules.auth.domain.RefreshToken;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, UUID>, RefreshTokenRepositoryPort {

	Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);
}
