package com.example.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.Reservation;
import com.example.model.ReservationSlot;
import com.example.model.Room;

import jakarta.transaction.Transactional;

@Repository
public interface ReservationSlotRepository extends JpaRepository<ReservationSlot, Long> {

	@Query("SELECT rs FROM ReservationSlot rs JOIN rs.reservation r WHERE r.room.id = :roomId AND rs.slotTime IN :slotTimes AND r.status <> 'CANCELLED'")
	List<ReservationSlot> findConflictingSlots(@Param("roomId") Long roomId,
			@Param("slotTimes") List<LocalDateTime> slotTimes);

	@Modifying
	@Transactional
	void deleteByReservation(Reservation reservation);

	@Query("SELECT rs FROM ReservationSlot rs JOIN rs.reservation r WHERE r.room = :room AND rs.slotTime >= :startTime AND rs.slotTime < :endTime AND r.status <> 'CANCELLED'")
	List<ReservationSlot> findByRoomAndSlotTimeBetween(@Param("room") Room room,
			@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
	
}
