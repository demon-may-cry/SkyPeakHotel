package com.skypeak.hotel.repository;

import com.skypeak.hotel.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
public interface RoomRepository extends JpaRepository<RoomEntity, UUID> {
    List<RoomEntity> findByActiveTrue();
}