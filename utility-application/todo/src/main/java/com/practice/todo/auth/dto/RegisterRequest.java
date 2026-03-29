package com.practice.todo.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

	@NotBlank
	@Email
	private String email;

	@NotBlank
	@Size(min = 8, max = 200)
	private String password;

	@NotBlank
	@Size(max = 200)
	private String displayName;
}
