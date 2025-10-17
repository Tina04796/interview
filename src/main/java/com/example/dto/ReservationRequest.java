package com.example.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {

	@NotNull(message = "Room ID is required")
	private Long roomId;

	@NotEmpty(message = "At least one time slot is required for reservation.")
	private List<LocalDateTime> selectedSlotTimes;

}