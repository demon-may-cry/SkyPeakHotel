package com.skypeak.hotel.repository;

import com.skypeak.hotel.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    @EntityGraph(attributePaths = "roleEntity")
    Optional<UserEntity> findByEmail(String email);
}