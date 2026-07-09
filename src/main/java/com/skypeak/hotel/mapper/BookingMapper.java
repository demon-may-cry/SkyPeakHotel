package com.skypeak.hotel.mapper;

import com.skypeak.hotel.dto.booking.BookingResponse;
import com.skypeak.hotel.entity.BookingEntity;
import com.skypeak.hotel.mapper.config.CentralMapperConfig;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Маппер для преобразования между BookingEntity и BookingResponse DTO.
 * <p>
 * Отвечает за конвертацию полей сущности бронирования в Data Transfer Object
 * для передачи клиентам через REST API. Использует MapStruct для автоматической
 * генерации реализации.
 * </p>
 *
 * <h3>Особенности маппинга:</h3>
 * <ul>
 *   <li>Поле {@code roomId} маппится из {@code room.id} сущности</li>
 *   <li>Поле {@code checkIn} маппится из {@code checkInDate} сущности</li>
 *   <li>Поле {@code checkOut} маппится из {@code checkOutDate} сущности</li>
 *   <li>Использует центральную конфигурацию {@link CentralMapperConfig}</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see BookingEntity
 * @see BookingResponse
 * @see CentralMapperConfig
 */
@Mapper(config = CentralMapperConfig.class)
public interface BookingMapper {

    /**
     * Преобразует сущность бронирования в DTO для передачи клиенту.
     * <p>
     * Маппит все поля сущности, включая вложенные поля комнаты и дат.
     * </p>
     *
     * @param booking сущность бронирования из базы данных
     * @return DTO бронирования для отправки клиенту
     */
    @BeanMapping(ignoreUnmappedSourceProperties = {
            "user",
            "pending",
            "confirmed",
            "cancelled",
            "checkedOut",
            "active",
            "nights",
            "pricePerNight"
    })
    @Mapping(source = "room.id", target = "roomId")
    @Mapping(source = "room.roomNumber", target = "roomNumber")
    @Mapping(source = "room.roomType.slug", target = "roomType")
    @Mapping(source = "checkInDate", target = "checkIn")
    @Mapping(source = "checkOutDate", target = "checkOut")
    BookingResponse toDto(BookingEntity booking);
}
