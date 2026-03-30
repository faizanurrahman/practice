package com.practice.todo.modules.auth.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {

	private String accessToken;
	private String refreshToken;
}
