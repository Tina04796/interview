package com.example.controller;

import com.example.dto.LoginRequest;
import com.example.dto.RegisterRequest;
import com.example.dto.UserResponse;
import com.example.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
		UserResponse registrationResult = userService.register(request);
		return ResponseEntity.ok(registrationResult);
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
		String token = userService.login(request);
		return ResponseEntity.ok(Map.of("token", token));
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserResponse> getUserById(@PathVariable("id") Long id) {
		UserResponse user = userService.getUserById(id);
		return ResponseEntity.ok(user);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}

}
