package com.practice.todo.security;

import com.practice.todo.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	private final JwtProperties props;
	private final SecretKey key;

	public JwtService(JwtProperties props) {
		this.props = props;
		this.key = Keys.hmacShaKeyFor(ensureMinLength(props.getSecret().getBytes(java.nio.charset.StandardCharsets.UTF_8)));
	}

	private static byte[] ensureMinLength(byte[] raw) {
		if (raw.length >= 32) {
			return raw;
		}
		byte[] padded = new byte[32];
		System.arraycopy(raw, 0, padded, 0, Math.min(raw.length, 32));
		return padded;
	}

	public String createAccessToken(UUID userId, String email) {
		Instant now = Instant.now();
		Instant exp = now.plusSeconds(props.getAccessTokenMinutes() * 60);
		return Jwts.builder()
				.subject(userId.toString())
				.claim("email", email)
				.issuedAt(Date.from(now))
				.expiration(Date.from(exp))
				.signWith(key)
				.compact();
	}

	public UUID parseUserId(String token) {
		Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
		return UUID.fromString(claims.getSubject());
	}

	public boolean isValid(String token) {
		try {
			Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
