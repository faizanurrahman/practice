package com.practice.todo.auth;

import com.practice.todo.auth.dto.LoginRequest;
import com.practice.todo.auth.dto.RefreshRequest;
import com.practice.todo.auth.dto.RegisterRequest;
import com.practice.todo.auth.dto.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	TokenResponse register(@Valid @RequestBody RegisterRequest body) {
		return authService.register(body);
	}

	@PostMapping("/login")
	TokenResponse login(@Valid @RequestBody LoginRequest body) {
		return authService.login(body);
	}

	@PostMapping("/refresh")
	TokenResponse refresh(@Valid @RequestBody RefreshRequest body) {
		return authService.refresh(body);
	}

	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void logout(@Valid @RequestBody RefreshRequest body) {
		authService.logout(body);
	}
}
