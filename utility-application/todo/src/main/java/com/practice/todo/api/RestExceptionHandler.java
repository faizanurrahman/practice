package com.practice.todo.api;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException e) {
		return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
	}

	@ExceptionHandler(BadCredentialsException.class)
	ResponseEntity<Map<String, String>> unauthorized(BadCredentialsException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
	}

	@ExceptionHandler(ObjectOptimisticLockingFailureException.class)
	ResponseEntity<Map<String, String>> optimisticLock(ObjectOptimisticLockingFailureException e) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(Map.of("message", "Resource was modified concurrently; refresh and retry"));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<Map<String, String>> validation(MethodArgumentNotValidException e) {
		FieldError fe = e.getBindingResult().getFieldError();
		String msg = fe != null ? fe.getDefaultMessage() : "Validation failed";
		return ResponseEntity.badRequest().body(Map.of("message", msg));
	}
}
