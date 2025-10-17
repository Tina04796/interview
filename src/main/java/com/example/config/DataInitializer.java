package com.example.config;

import com.example.model.Role;
import com.example.model.User;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		boolean adminExists = userRepository.findAll().stream().anyMatch(user -> user.getRole() == Role.ADMIN);
		if (!adminExists) {
			User admin = User.builder().username("admin").password(passwordEncoder.encode("admin123"))
					.email("admin@example.com").role(Role.ADMIN).build();
			userRepository.save(admin);
			System.out.println("Init: create default admin (admin/admin123)");
		}
	}

}