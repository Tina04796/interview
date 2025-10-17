package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByUsername(String username);

	@Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
	Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);

}