package com.skypeak.hotel.dto;

import com.skypeak.hotel.entity.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
@Getter
@AllArgsConstructor
public class RoomResponse {
    private final UUID id;
    private final String roomNumber;
    private final RoomType roomType;
    private final BigDecimal pricePerNight;
    private final boolean active;
    private final String description;
}
