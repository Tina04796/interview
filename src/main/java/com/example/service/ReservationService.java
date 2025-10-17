package com.example.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.dto.ReservationRequest;
import com.example.dto.ReservationResponse;

public interface ReservationService {

	ReservationResponse createReservation(ReservationRequest request, Long currentUserId);

	ReservationResponse getReservationById(Long reservationId);

	List<ReservationResponse> getReservationsByUser(Long userId);

	List<ReservationResponse> getReservationsByRoomAndDateRange(Long roomId, LocalDate startDate, LocalDate endDate);

	void cancelReservation(Long reservationId, Long currentUserId);

	List<LocalDateTime> getAvailableSlots(Long roomId, LocalDate date);

}
