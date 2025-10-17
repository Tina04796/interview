package com.example.service;

import com.example.dto.*;

public interface UserService {

	UserResponse register(RegisterRequest request);

	String login(LoginRequest request);

	UserResponse getUserById(Long id);

	void deleteUser(Long id);

}
