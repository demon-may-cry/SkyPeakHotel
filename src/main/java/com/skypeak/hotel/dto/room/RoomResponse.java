package com.skypeak.hotel.dto.room;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO для представления данных комнаты в ответах API.
 * <p>
 * Содержит полную информацию о комнате: ID, номер, тип, цену за ночь, статус и описание.
 *
 * @param id             уникальный идентификатор комнаты (UUID).
 * @param roomNumber     номер комнаты в отеле.
 * @param roomType       тип комнаты (STANDARD, APARTMENT, SUITE и т.д.).
 * @param pricePerNight  цена комнаты за одну ночь.
 * @param active         флаг доступности комнаты для бронирования.
 * @param description    описание/характеристики комнаты.
 * @author Дмитрий Ельцов
 */
public record RoomResponse(
        UUID id,
        String roomNumber,
        String roomType,
        BigDecimal basePrice,
        boolean active) {
}
