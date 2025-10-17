package com.example.controller;

import com.example.dto.ReservationRequest;
import com.example.dto.ReservationResponse;
import com.example.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // 導入 Authentication
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:8081")
@RequiredArgsConstructor
public class ReservationController {

	private final ReservationService reservationService;

	@PostMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request,
			Authentication authentication) {
		Long currentUserId = Long.valueOf(authentication.getName());
		ReservationResponse response = reservationService.createReservation(request, currentUserId);
		return ResponseEntity.created(URI.create("/api/reservations/" + response.getId())).body(response);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long id) {
		ReservationResponse response = reservationService.getReservationById(id);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/user/{userId}")
	@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
	public ResponseEntity<List<ReservationResponse>> getReservationsByUser(@PathVariable Long userId) {
		List<ReservationResponse> response = reservationService.getReservationsByUser(userId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/room/{roomId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<ReservationResponse>> getReservationsByRoomAndDateRange(@PathVariable Long roomId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		List<ReservationResponse> response = reservationService.getReservationsByRoomAndDateRange(roomId, startDate,
				endDate);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Void> cancelReservation(@PathVariable Long id, Authentication authentication) {
		Long currentUserId = Long.valueOf(authentication.getName());
		reservationService.cancelReservation(id, currentUserId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/slots/{roomId}")
	public ResponseEntity<List<LocalDateTime>> getAvailableSlots(@PathVariable Long roomId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		List<LocalDateTime> availableSlots = reservationService.getAvailableSlots(roomId, date);
		return ResponseEntity.ok(availableSlots);
	}

}
