package com.skypeak.hotel.repository;

import com.skypeak.hotel.entity.UserBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
public interface UserBalanceRepository extends JpaRepository<UserBalanceEntity, UUID> {

    Optional<UserBalanceEntity> findByUser_Id(UUID uuid);

    boolean existsByUser_Id(UUID uuid);
}