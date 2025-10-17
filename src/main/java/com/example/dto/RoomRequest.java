package com.example.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest {

	@NotBlank(message = "Room name required")
	private String name;

	private String location;

	@Min(value = 1, message = "Must be greater than 0")
	private Integer capacity;

}
