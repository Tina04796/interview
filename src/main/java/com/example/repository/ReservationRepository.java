package com.example.repository;

import com.example.model.Reservation;
import com.example.model.Room;
import com.example.model.User;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	List<Reservation> findByUser(User user);

	List<Reservation> findByRoomAndStartTimeBetween(Room room, LocalDateTime startTime, LocalDateTime endTime);

	@Modifying
	@Transactional
	void deleteByUser(User user);

}