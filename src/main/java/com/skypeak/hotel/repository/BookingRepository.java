package com.skypeak.hotel.repository;

import com.skypeak.hotel.entity.BookingEntity;
import com.skypeak.hotel.entity.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
public interface BookingRepository extends JpaRepository<BookingEntity, UUID> {
    Page<BookingEntity> findByUser_IdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    boolean existsByRoom_IdAndStatusNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            UUID id,
            BookingStatus status,
            LocalDate checkInDate,
            LocalDate checkOutDate);
}