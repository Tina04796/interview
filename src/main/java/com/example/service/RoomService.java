package com.example.service;

import java.util.List;

import com.example.dto.RoomRequest;
import com.example.dto.RoomResponse;

public interface RoomService {

	List<RoomResponse> getAllRooms();

	RoomResponse getRoomById(Long id);

	RoomResponse createRoom(RoomRequest roomRequest);

	RoomResponse updateRoom(Long id, RoomRequest roomRequest);

	void deleteRoom(Long id);

}
