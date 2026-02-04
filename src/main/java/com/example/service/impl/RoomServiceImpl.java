package com.example.service.impl;

import com.example.model.Room;
import com.example.model.RoomImage;
import com.example.dto.*;
import com.example.repository.RoomRepository;
import com.example.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

	private final RoomRepository roomRepository;
	private final String UPLOAD_DIR = "uploads/room-images/";
	private final String IMAGE_BASE_URL = "/api/images/";

	@Override
	@Transactional(readOnly = true)
	public List<RoomResponse> getAllRooms() {
		return roomRepository.findAll().stream().map(this::convertToResponse).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public RoomResponse getRoomById(Long id) {
		return roomRepository.findById(id).map(this::convertToResponse)
				.orElseThrow(() -> new RuntimeException("Room not found with ID: " + id));
	}

	@Override
	@Transactional
	public RoomResponse createRoom(RoomRequest roomRequest, MultipartFile[] files) {
		if (roomRepository.existsByName(roomRequest.getName())) {
			throw new RuntimeException("Room name already exists: " + roomRequest.getName());
		}
		Room room = convertToEntity(roomRequest);
		if (files != null && files.length > 0) {
			handleImageUpload(room, files);
		}
		Room saved = roomRepository.save(room);
		return convertToResponse(saved);
//		return convertToResponse(roomRepository.save(room));
	}

	@Override
	@Transactional
	public RoomResponse updateRoom(Long id, RoomRequest roomRequest, MultipartFile[] newFiles,
			List<Long> removeImageIds) {
		Room room = roomRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Room not found with ID: " + id));
		if (roomRepository.existsByNameAndIdNot(roomRequest.getName(), id)) {
			throw new RuntimeException("Room name already exists: " + roomRequest.getName());
		}
		room.setName(roomRequest.getName());
		room.setLocation(roomRequest.getLocation());
		room.setCapacity(roomRequest.getCapacity());
		if (removeImageIds != null && !removeImageIds.isEmpty()) {
			room.getImages().removeIf(img -> {
				if (removeImageIds.contains(img.getId())) {
					deletePhysicalFile(img.getFilePath());
					return true;
				}
				return false;
			});
		}
		if (newFiles != null && newFiles.length > 0) {
			boolean hasActualFiles = java.util.Arrays.stream(newFiles).anyMatch(f -> !f.isEmpty());
			if (hasActualFiles) {
				handleImageUpload(room, newFiles);
			}
		}
		Room savedRoom = roomRepository.save(room);
		return convertToResponse(savedRoom);
	}

	@Override
	@Transactional
	public void deleteRoom(Long id) {
		Room room = roomRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Room not found with ID: " + id));
		room.getImages().forEach(img -> deletePhysicalFile(img.getFilePath()));
		roomRepository.delete(room);
	}

	private void handleImageUpload(Room room, MultipartFile[] files) {
		try {
			Path uploadPath = Paths.get(UPLOAD_DIR);
			if (!Files.exists(uploadPath))
				Files.createDirectories(uploadPath);

			for (MultipartFile file : files) {
				if (file.isEmpty())
					continue;
				String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
				Path path = uploadPath.resolve(fileName);
				Files.write(path, file.getBytes());

				RoomImage img = RoomImage.builder().imageUrl(IMAGE_BASE_URL + fileName).filePath(path.toString())
						.room(room).build();
				room.addImage(img);
			}
		} catch (IOException e) {
			throw new RuntimeException("檔案儲存失敗", e);
		}
	}

	private void deletePhysicalFile(String filePath) {
		try {
			Files.deleteIfExists(Paths.get(filePath));
		} catch (IOException ignored) {
		}
	}

	private Room convertToEntity(RoomRequest request) {
		return Room.builder().name(request.getName()).location(request.getLocation()).capacity(request.getCapacity())
				.build();
	}

	private RoomResponse convertToResponse(Room room) {
		return RoomResponse.builder().id(room.getId()).name(room.getName()).location(room.getLocation())
				.capacity(room.getCapacity())
				.images(room.getImages().stream()
						.map(img -> RoomResponse.ImageInfo.builder().id(img.getId()).url(img.getImageUrl()).build())
						.collect(Collectors.toList()))
				.build();
	}
}
