package com.example.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.dto.RoomRequest;
import com.example.dto.RoomResponse;

public interface RoomService {

	List<RoomResponse> getAllRooms();
	RoomResponse getRoomById(Long id);
	RoomResponse createRoom(RoomRequest roomRequest, MultipartFile[] files);
	RoomResponse updateRoom(Long id, RoomRequest roomRequest, MultipartFile[] newFiles, List<Long> removeImageIds);
	void deleteRoom(Long id);

}
