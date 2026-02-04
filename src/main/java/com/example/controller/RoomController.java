package com.example.controller;

import com.example.dto.*;
import com.example.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.net.URI;
import java.util.List;

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

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<RoomResponse> createRoom(
	        @RequestPart("room") @Valid RoomRequest roomRequest,
	        @RequestPart(value = "files", required = false) MultipartFile[] files) {
	    RoomResponse saved = roomService.createRoom(roomRequest, files);
	    return ResponseEntity.created(URI.create("/api/rooms/" + saved.getId())).body(saved);
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<RoomResponse> updateRoom(
	        @PathVariable Long id,
	        @RequestPart("room") @Valid RoomRequest roomRequest,
	        @RequestPart(value = "newFiles", required = false) MultipartFile[] newFiles,
	        @RequestParam(value = "removeImageIds", required = false) List<Long> removeImageIds) {
	    RoomResponse updated = roomService.updateRoom(id, roomRequest, newFiles, removeImageIds);
	    return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
		roomService.deleteRoom(id);
		return ResponseEntity.noContent().build();
	}

}
