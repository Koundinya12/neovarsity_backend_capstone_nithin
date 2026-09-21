package com.ecom.userservice.repositories;

import com.ecom.userservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findById(String id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}