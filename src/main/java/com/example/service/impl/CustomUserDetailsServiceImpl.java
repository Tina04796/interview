package com.example.service.impl;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.model.CustomUserDetails;
import com.example.model.User;
import com.example.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public CustomUserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
		Optional<User> userOptional;
		try {
			Long userId = Long.valueOf(identifier);
			userOptional = userRepository.findById(userId);
		} catch (NumberFormatException e) {
			userOptional = userRepository.findByUsernameOrEmail(identifier);
		}
		User user = userOptional
				.orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + identifier));
		return new CustomUserDetails(user);
	}

}
