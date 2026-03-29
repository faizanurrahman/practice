package com.practice.todo.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

	@NotBlank
	private String secret;

	@Min(1)
	private long accessTokenMinutes = 15;

	@Min(1)
	private long refreshTokenDays = 14;
}
