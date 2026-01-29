package com.skypeak.hotel.dto.booking;

import com.skypeak.hotel.entity.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
@Getter
@AllArgsConstructor
public class BookingResponse {
    private final UUID id;
    private final UUID roomId;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final BookingStatus status;
    private final LocalDateTime createdAt;
}
