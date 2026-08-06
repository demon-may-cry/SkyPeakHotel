package com.skypeak.hotel.dto.booking;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO для запроса на создание нового бронирования.
 * <p>
 * Содержит необходимую информацию для создания бронирования: ID комнаты и даты заезда/выезда.
 *
 * @author Дмитрий Ельцов
 */
@Getter
@Setter
public class BookingRequest {

    /**
     * Уникальный slug типа номера (standard, apartments, suite).
     * Не должна быть пустой.
     */
    @NotBlank
    private String roomTypeSlug;

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

    @NotNull
    @Min(value = 1, message = "Количество гостей должно быть положительным")
    private Integer guestsCount;
}
