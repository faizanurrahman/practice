package com.practice.todo.modules.auth.application;

import com.practice.todo.config.JwtProperties;
import com.practice.todo.modules.auth.api.dto.LoginRequest;
import com.practice.todo.modules.auth.api.dto.RefreshRequest;
import com.practice.todo.modules.auth.api.dto.RegisterRequest;
import com.practice.todo.modules.auth.api.dto.TokenResponse;
import com.practice.todo.modules.auth.application.port.RefreshTokenRepositoryPort;
import com.practice.todo.modules.auth.domain.RefreshToken;
import com.practice.todo.modules.user.application.port.UserAuthPort;
import com.practice.todo.modules.user.domain.User;
import com.practice.todo.security.JwtService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserAuthPort userAuthPort;
	private final RefreshTokenRepositoryPort refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final JwtProperties jwtProperties;
	private final AuthenticationManager authenticationManager;
	private final SecureRandom secureRandom = new SecureRandom();

	@Transactional
	public TokenResponse register(RegisterRequest req) {
		if (userAuthPort.existsByEmailIgnoreCase(req.getEmail())) {
			throw new IllegalArgumentException("Email already registered");
		}
		User u = new User();
		u.setEmail(req.getEmail().trim().toLowerCase());
		u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
		u.setDisplayName(req.getDisplayName().trim());
		userAuthPort.save(u);
		return issueTokens(u);
	}

	@Transactional
	public TokenResponse login(LoginRequest req) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(req.getEmail().trim().toLowerCase(), req.getPassword()));
		User u = userAuthPort
				.findByEmailIgnoreCase(req.getEmail().trim().toLowerCase())
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		return issueTokens(u);
	}

	@Transactional
	public TokenResponse refresh(RefreshRequest req) {
		String hash = sha256Hex(req.getRefreshToken());
		RefreshToken rt = refreshTokenRepository
				.findByTokenHashAndRevokedFalse(hash)
				.orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
		if (rt.getExpiresAt().isBefore(Instant.now())) {
			throw new IllegalArgumentException("Refresh token expired");
		}
		rt.setRevoked(true);
		refreshTokenRepository.save(rt);
		User u = rt.getUser();
		return issueTokens(u);
	}

	@Transactional
	public void logout(RefreshRequest req) {
		String hash = sha256Hex(req.getRefreshToken());
		refreshTokenRepository
				.findByTokenHashAndRevokedFalse(hash)
				.ifPresent(rt -> {
					rt.setRevoked(true);
					refreshTokenRepository.save(rt);
				});
	}

	private TokenResponse issueTokens(User u) {
		String access = jwtService.createAccessToken(u.getId(), u.getEmail());
		byte[] raw = new byte[48];
		secureRandom.nextBytes(raw);
		String refreshPlain = Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
		RefreshToken rt = new RefreshToken();
		rt.setUser(u);
		rt.setTokenHash(sha256Hex(refreshPlain));
		rt.setExpiresAt(Instant.now().plusSeconds(jwtProperties.getRefreshTokenDays() * 24 * 3600));
		refreshTokenRepository.save(rt);
		return new TokenResponse(access, refreshPlain);
	}

	private static String sha256Hex(String value) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hash = md.digest(value.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}
}
