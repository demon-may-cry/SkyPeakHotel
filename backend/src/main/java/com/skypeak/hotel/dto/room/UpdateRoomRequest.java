package com.skypeak.hotel.dto.room;

import com.skypeak.hotel.entity.enums.RoomType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO для запроса на обновление информации о комнате.
 * <p>
 * Содержит информацию для обновления: тип, цену за ночь, описание и статус активности.
 * Все поля являются опциональными.
 *
 * @author Дмитрий Ельцов
 */
@Getter
@Setter
public class UpdateRoomRequest {

    /**
     * Новый номер комнаты.
     * Опционально - если не указано, остается без изменений.
     */
    private String roomNumber;

    /**
     * Новый тип комнаты.
     * Опционально - если не указано, остается без изменений.
     */
    private String roomType;

/*    *//**
     * Новая цена комнаты за одну ночь.
     * Опционально - если не указано, остается без изменений.
     *//*
    private BigDecimal pricePerNight;

    *//**
     * Новое описание комнаты.
     * Опционально - если не указано, остается без изменений.
     *//*
    private String description;*/

    /**
     * Флаг активности комнаты (доступна/недоступна для бронирования).
     * Опционально - если не указано, остается без изменений.
     */
    private Boolean active;
}
