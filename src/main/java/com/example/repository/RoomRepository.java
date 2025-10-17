package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

	boolean existsByName(String name);

	boolean existsByNameAndIdNot(String name, Long id);

}