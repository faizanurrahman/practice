package com.practice.todo.modules.auth.application.port;

import com.practice.todo.modules.auth.domain.RefreshToken;
import java.util.Optional;

public interface RefreshTokenRepositoryPort {

	Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);

	RefreshToken save(RefreshToken token);
}
