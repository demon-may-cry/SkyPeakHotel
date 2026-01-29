package com.skypeak.hotel.dto.booking;

import com.skypeak.hotel.entity.enums.BookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
public record BookingResponse(
        UUID id,
        UUID roomId,
        LocalDate checkIn,
        LocalDate checkOut,
        BookingStatus status,
        LocalDateTime createdAt) {
}
