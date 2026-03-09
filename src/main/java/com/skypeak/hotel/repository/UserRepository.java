package com.skypeak.hotel.repository;

import com.skypeak.hotel.entity.UserEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    @EntityGraph(attributePaths = "role")
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = "role")
    @NonNull
    Page<UserEntity> findAll(@NonNull Pageable pageable);

    @EntityGraph(attributePaths = "role")
    @NonNull
    Optional<UserEntity> findById(UUID id);
}