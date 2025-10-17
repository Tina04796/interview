package com.example.service.impl;

import com.example.dto.LoginRequest;
import com.example.dto.RegisterRequest;
import com.example.dto.UserResponse;
import com.example.exception.AuthenticationException;
import com.example.exception.ResourceConflictException;
import com.example.exception.ResourceNotFoundException;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.security.JwtUtil;
import com.example.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Override
	public UserResponse register(RegisterRequest request) {
		if (userRepository.findByUsernameOrEmail(request.getUsername()).isPresent()
				|| userRepository.findByUsernameOrEmail(request.getEmail()).isPresent()) {
			throw new ResourceConflictException("Username or email already exists.");
		}
		String encodedPassword = passwordEncoder.encode(request.getPassword());
		User user = convertToEntity(request, encodedPassword);
		User saved = userRepository.save(user);
		return convertToResponse(saved);
	}

	@Override
	public String login(LoginRequest request) {
		User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail())
				.orElseThrow(() -> new AuthenticationException("Invalid username or password."));
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new AuthenticationException("Invalid username or password.");
		}
		return jwtUtil.generateToken(user.getId(), user.getRole());
	}

	@Override
	public UserResponse getUserById(Long id) {
		return userRepository.findById(id).map(this::convertToResponse)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
	}

	@Override
	public void deleteUser(Long id) {
		if (!userRepository.existsById(id)) {
			throw new ResourceNotFoundException("User not found with ID: " + id);
		}
		userRepository.deleteById(id);
	}

	private UserResponse convertToResponse(User user) {
		return UserResponse.builder().id(user.getId()).username(user.getUsername()).role(user.getRole())
				.email(user.getEmail()).build();
	}

	private User convertToEntity(RegisterRequest request, String encodedPassword) {
		return User.builder().username(request.getUsername()).password(encodedPassword).email(request.getEmail())
				.role(Role.USER).build();
	}

}