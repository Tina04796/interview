package com.example.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.dto.RoomRequest;
import com.example.dto.RoomResponse;
import com.example.service.RoomService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

	private final RoomService roomService;

	@GetMapping
	public ResponseEntity<List<RoomResponse>> getAllRooms() {
		List<RoomResponse> result = roomService.getAllRooms();
		return ResponseEntity.ok(result);
	}

	@GetMapping("/{id}")
	public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long id) {
		RoomResponse result = roomService.getRoomById(id);
		return ResponseEntity.ok(result);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody RoomRequest roomRequest) {
		RoomResponse saved = roomService.createRoom(roomRequest);
		return ResponseEntity.created(URI.create("/api/rooms/" + saved.getId())).body(saved);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomRequest roomRequest) {
		RoomResponse updated = roomService.updateRoom(id, roomRequest);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
		roomService.deleteRoom(id);
		return ResponseEntity.noContent().build();
	}

}
