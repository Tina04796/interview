package com.example.service.impl;

import com.example.dto.ReservationRequest;
import com.example.dto.ReservationResponse;
import com.example.exception.AuthenticationException;
import com.example.exception.ResourceConflictException;
import com.example.exception.ResourceNotFoundException;
import com.example.model.Reservation;
import com.example.model.ReservationSlot;
import com.example.model.ReservationStatus;
import com.example.model.Room;
import com.example.model.User;
import com.example.repository.ReservationRepository;
import com.example.repository.ReservationSlotRepository;
import com.example.repository.RoomRepository;
import com.example.repository.UserRepository;
import com.example.service.ReservationService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

	private final ReservationRepository reservationRepository;
	private final ReservationSlotRepository reservationSlotRepository;
	private final UserRepository userRepository;
	private final RoomRepository roomRepository;

	private static final long SLOT_DURATION_MINUTES = 30;

	private static final LocalTime START_OF_DAY = LocalTime.of(8, 0);
	private static final LocalTime END_OF_DAY = LocalTime.of(20, 30);

	@Override
	@Transactional
	public ReservationResponse createReservation(ReservationRequest request, Long currentUserId) {
		User user = userRepository.findById(currentUserId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + currentUserId));
		Room room = roomRepository.findById(request.getRoomId())
				.orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + request.getRoomId()));
		List<LocalDateTime> sortedSlots = new ArrayList<>(request.getSelectedSlotTimes());
		sortedSlots.sort(Comparator.naturalOrder());
		if (sortedSlots.isEmpty()) {
			throw new ResourceConflictException("Reservation must include at least one time slot.");
		}
		if (!isSlotsContinuous(sortedSlots)) {
			throw new ResourceConflictException(
					"Selected slots must be continuous and exactly " + SLOT_DURATION_MINUTES + " minutes apart.");
		}
		List<ReservationSlot> conflicts = reservationSlotRepository.findConflictingSlots(room.getId(), sortedSlots);
		if (!conflicts.isEmpty()) {
			throw new ResourceConflictException(
					"One or more selected slots are already reserved for room ID: " + room.getId());
		}
		Reservation reservation = convertToEntity(request, user, room, sortedSlots);
		Reservation savedReservation = reservationRepository.save(reservation);
		List<ReservationSlot> slots = sortedSlots.stream().map(slotTime -> {
			return ReservationSlot.builder().reservation(savedReservation).slotTime(slotTime).build();
		}).collect(Collectors.toList());
		reservationSlotRepository.saveAll(slots);
		return convertToResponse(savedReservation, user.getUsername(), room.getName());
	}

	@Override
	public ReservationResponse getReservationById(Long reservationId) {
		Reservation reservation = reservationRepository.findById(reservationId)
				.orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + reservationId));
		String username = reservation.getUser().getUsername();
		String roomName = reservation.getRoom().getName();
		return convertToResponse(reservation, username, roomName);
	}

	@Override
	public List<ReservationResponse> getReservationsByUser(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
		List<Reservation> reservations = reservationRepository.findByUser(user);
		return reservations.stream()
				.map(res -> convertToResponse(res, res.getUser().getUsername(), res.getRoom().getName()))
				.collect(Collectors.toList());
	}

	@Override
	public List<ReservationResponse> getReservationsByRoomAndDateRange(Long roomId, LocalDate startDate,
			LocalDate endDate) {
		Room room = roomRepository.findById(roomId)
				.orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
		List<Reservation> reservations = reservationRepository.findByRoomAndStartTimeBetween(room, startDateTime,
				endDateTime);
		return reservations.stream()
				.map(res -> convertToResponse(res, res.getUser().getUsername(), res.getRoom().getName()))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void cancelReservation(Long reservationId, Long currentUserId) {
		Reservation reservation = reservationRepository.findById(reservationId)
				.orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + reservationId));
		if (!reservation.getUser().getId().equals(currentUserId)) {
			throw new AuthenticationException("You are not authorized to cancel this reservation.");
		}
		if (reservation.getStatus() == ReservationStatus.CANCELLED) {
			return;
		}
		reservation.setStatus(ReservationStatus.CANCELLED);
		reservationRepository.save(reservation);
	}

	@Override
	public List<LocalDateTime> getAvailableSlots(Long roomId, LocalDate date) {
		Room room = roomRepository.findById(roomId)
				.orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));
		List<LocalDateTime> allPotentialSlots = generatePotentialSlots(date);
		List<ReservationSlot> reservedSlotEntities = reservationSlotRepository.findByRoomAndSlotTimeBetween(room,
				date.atStartOfDay(), date.plusDays(1).atStartOfDay());
		List<LocalDateTime> reservedSlots = reservedSlotEntities.stream().map(ReservationSlot::getSlotTime)
				.collect(Collectors.toList());
		return allPotentialSlots.stream().filter(slot -> !reservedSlots.contains(slot)).collect(Collectors.toList());
	}

	private boolean isSlotsContinuous(List<LocalDateTime> sortedSlots) {
		if (sortedSlots.size() <= 1) {
			return true;
		}
		for (int i = 0; i < sortedSlots.size() - 1; i++) {
			LocalDateTime current = sortedSlots.get(i);
			LocalDateTime next = sortedSlots.get(i + 1);
			if (ChronoUnit.MINUTES.between(current, next) != SLOT_DURATION_MINUTES) {
				return false;
			}
		}
		return true;
	}

	private List<LocalDateTime> generatePotentialSlots(LocalDate date) {
		List<LocalDateTime> slots = new ArrayList<>();
		LocalDateTime currentSlot = date.atTime(START_OF_DAY);
		LocalDateTime endBoundary = date.atTime(END_OF_DAY);
		while (currentSlot.isBefore(endBoundary)) {
			slots.add(currentSlot);
			currentSlot = currentSlot.plusMinutes(SLOT_DURATION_MINUTES);
		}
		return slots;
	}

	private Reservation convertToEntity(ReservationRequest request, User user, Room room,
			List<LocalDateTime> sortedSlots) {
		LocalDateTime startTime = sortedSlots.get(0);
		LocalDateTime endTime = sortedSlots.get(sortedSlots.size() - 1).plusMinutes(SLOT_DURATION_MINUTES);
		return Reservation.builder().user(user).room(room).startTime(startTime).endTime(endTime)
				.status(ReservationStatus.CONFIRMED).build();
	}

	private ReservationResponse convertToResponse(Reservation reservation, String username, String roomName) {
		return ReservationResponse.builder().id(reservation.getId()).userId(reservation.getUser().getId())
				.username(username).roomId(reservation.getRoom().getId()).roomName(roomName)
				.startTime(reservation.getStartTime()).endTime(reservation.getEndTime()).status(reservation.getStatus())
				.createdAt(reservation.getCreatedAt()).build();
	}

	@Override
	public boolean isOwner(Long reservationId, Long userId) {
		return reservationRepository.findById(reservationId).map(reservation -> {
			return reservation.getUser().getId().equals(userId);
		}).orElse(false);
	}

}
