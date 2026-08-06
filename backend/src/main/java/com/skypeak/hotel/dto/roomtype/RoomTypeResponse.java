package com.skypeak.hotel.dto.roomtype;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO для представления информации о типе номера.
 *
 * @param id          уникальный идентификатор типа номера.
 * @param slug        уникальный строковый идентификатор (standard, suite и т.д.).
 * @param title       отображаемое название категории.
 * @param description описание характеристик данной категории номеров.
 * @param basePrice   базовая стоимость проживания за ночь.
 * @author Дмитрий Ельцов
 */
public record RoomTypeResponse(
        UUID id,
        String slug,
        String title,
        String description,
        BigDecimal basePrice
) {
}
