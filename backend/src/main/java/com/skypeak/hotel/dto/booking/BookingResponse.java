package com.skypeak.hotel.dto.booking;

import com.skypeak.hotel.entity.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для представления данных бронирования в ответах API.
 * <p>
 * Содержит полную информацию о бронировании: ID, комнате, датах заезда/выезда, статусе и времени создания.
 *
 * @param id        уникальный идентификатор бронирования (UUID).
 * @param roomId    уникальный идентификатор комнаты (UUID).
 * @param checkIn   дата заезда пользователя.
 * @param checkOut  дата выезда пользователя.
 * @param status    статус бронирования (CONFIRMED, CANCELLED и т.д.).
 * @param createdAt дата и время создания бронирования.
 * @author Дмитрий Ельцов
 */
public record BookingResponse(
        UUID id,
        UUID roomId,
        String roomNumber,
        String roomTypeSlug,
        String roomTypeName,
        LocalDate checkIn,
        LocalDate checkOut,
        Integer guestsCount,
        BigDecimal totalPrice,
        BookingStatus status,
        LocalDateTime createdAt) {
}
