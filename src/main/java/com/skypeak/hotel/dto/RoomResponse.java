package com.skypeak.hotel.dto;

import com.skypeak.hotel.entity.enums.RoomType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
public record RoomResponse(
        UUID id,
        String roomNumber,
        RoomType roomType,
        BigDecimal pricePerNight,
        boolean active,
        String description) {
}
