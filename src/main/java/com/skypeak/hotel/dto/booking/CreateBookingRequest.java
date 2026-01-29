package com.skypeak.hotel.dto.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
@Getter
@Setter
public class CreateBookingRequest {

    @NotNull
    private UUID roomId;

    @NotNull
    private LocalDate checkIn;

    @NotNull
    private LocalDate checkOut;
}
