package com.example.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.dto.RoomRequest;
import com.example.dto.RoomResponse;
import com.example.exception.ResourceConflictException;
import com.example.exception.ResourceNotFoundException;
import com.example.model.Room;
import com.example.repository.RoomRepository;
import com.example.service.RoomService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

	private final RoomRepository roomRepository;

	@Override
	public List<RoomResponse> getAllRooms() {
		List<Room> rooms = roomRepository.findAll();
		return rooms.stream().map(this::convertToResponse).collect(Collectors.toList());
	}

	@Override
	public RoomResponse getRoomById(Long id) {
		return roomRepository.findById(id).map(this::convertToResponse)
				.orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + id));
	}

	@Override
	public RoomResponse createRoom(RoomRequest roomRequest) {
		if (roomRepository.existsByName(roomRequest.getName())) {
			throw new ResourceConflictException("Room name already exists: " + roomRequest.getName());
		}
		Room room = convertToEntity(roomRequest);
		Room saved = roomRepository.save(room);
		return convertToResponse(saved);
	}

	@Override
	public RoomResponse updateRoom(Long id, RoomRequest roomRequest) {
		return roomRepository.findById(id).map(room -> {
			if (roomRepository.existsByNameAndIdNot(roomRequest.getName(), id)) {
				throw new ResourceConflictException("Room name already exists: " + roomRequest.getName());
			}
			room.setName(roomRequest.getName());
			room.setLocation(roomRequest.getLocation());
			room.setCapacity(roomRequest.getCapacity());
			Room updated = roomRepository.save(room);
			return convertToResponse(updated);
		}).orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + id));
	}

	@Override
	public void deleteRoom(Long id) {
		if (!roomRepository.existsById(id)) {
			throw new ResourceNotFoundException("Room not found with ID: " + id);
		}
		roomRepository.deleteById(id);
	}

	private Room convertToEntity(RoomRequest request) {
		return Room.builder().name(request.getName()).location(request.getLocation()).capacity(request.getCapacity())
				.build();
	}

	private RoomResponse convertToResponse(Room room) {
		return RoomResponse.builder().id(room.getId()).name(room.getName()).location(room.getLocation())
				.capacity(room.getCapacity()).build();
	}
	
}
