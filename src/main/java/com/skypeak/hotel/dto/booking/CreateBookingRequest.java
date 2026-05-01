package com.skypeak.hotel.dto.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO для запроса на создание нового бронирования.
 * <p>
 * Содержит необходимую информацию для создания бронирования: ID комнаты и даты заезда/выезда.
 *
 * @author Дмитрий Ельцов
 */
@Getter
@Setter
public class CreateBookingRequest {

    /**
     * Уникальный идентификатор комнаты (UUID), которую хочет забронировать пользователь.
     * Не должна быть пустой.
     */
    @NotNull
    private UUID roomId;

    /**
     * Дата заезда пользователя.
     * Не должна быть пустой и должна быть в будущем.
     */
    @NotNull
    private LocalDate checkIn;

    /**
     * Дата выезда пользователя.
     * Не должна быть пустой и должна быть после даты заезда.
     */
    @NotNull
    private LocalDate checkOut;
}
