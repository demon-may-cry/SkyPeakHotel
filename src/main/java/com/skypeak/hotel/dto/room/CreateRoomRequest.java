package com.skypeak.hotel.dto.room;

import com.skypeak.hotel.entity.enums.RoomType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO для запроса на создание новой комнаты в отеле.
 * <p>
 * Содержит информацию о комнате: номер, тип, цену за ночь и описание.
 *
 * @author Дмитрий Ельцов
 */
@Getter
@Setter
public class CreateRoomRequest {

    /**
     * Номер комнаты в отеле.
     * Не должен быть пустым/null и должен содержать максимум 20 символов.
     */
    @NotNull
    @Size(max = 20)
    private String roomNumber;

    /**
     * Тип комнаты (STANDARD, APARTMENT, SUITE и т.д.).
     * Не должен быть пустым/null.
     */
    @NotNull
    private RoomType roomType;

    /**
     * Цена комнаты за одну ночь.
     * Не должна быть пустой/null.
     */
    @NotNull
    private BigDecimal pricePerNight;

    /**
     * Описание/характеристики комнаты.
     * Максимум 255 символов.
     */
    @Size(max = 255)
    private String description;
}
