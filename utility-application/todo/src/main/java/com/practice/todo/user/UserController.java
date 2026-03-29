package com.practice.todo.user;

import com.practice.todo.security.UserPrincipal;
import com.practice.todo.user.dto.PatchUserRequest;
import com.practice.todo.user.dto.UserResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/me")
	UserResponse me(@AuthenticationPrincipal UserPrincipal principal) {
		User u = userService.getById(principal.getId());
		return toResponse(u);
	}

	@PatchMapping("/me")
	UserResponse patchMe(
			@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody PatchUserRequest body) {
		UUID id = principal.getId();
		if (Boolean.TRUE.equals(body.getOnboardingComplete())) {
			userService.completeOnboarding(id);
		}
		if (body.getDisplayName() != null) {
			userService.updateProfile(id, body.getDisplayName());
		}
		return toResponse(userService.getById(id));
	}

	private static UserResponse toResponse(User u) {
		return UserResponse.builder()
				.id(u.getId())
				.email(u.getEmail())
				.displayName(u.getDisplayName())
				.onboardingCompletedAt(u.getOnboardingCompletedAt())
				.build();
	}
}
