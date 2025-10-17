package com.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

	@NotBlank(message = "Username required")
	private String username;

	@NotBlank(message = "Password required")
	private String password;

	@Email(message = "Invalid email")
	private String email;

}
