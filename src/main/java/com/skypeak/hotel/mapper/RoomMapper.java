package com.skypeak.hotel.mapper;

import com.skypeak.hotel.dto.room.RoomResponse;
import com.skypeak.hotel.entity.RoomEntity;
import com.skypeak.hotel.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;

/**
 * Маппер для преобразования между RoomEntity и RoomResponse DTO.
 * <p>
 * Отвечает за конвертацию полей сущности комнаты в Data Transfer Object
 * для передачи клиентам через REST API. Использует MapStruct для автоматической
 * генерации реализации.
 * </p>
 *
 * <h3>Особенности маппинга:</h3>
 * <ul>
 *   <li>Прямое маппинг всех полей сущности в DTO</li>
 *   <li>Использует центральную конфигурацию {@link CentralMapperConfig}</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see RoomEntity
 * @see RoomResponse
 * @see CentralMapperConfig
 */
@Mapper(config = CentralMapperConfig.class)
public interface RoomMapper {

    /**
     * Преобразует сущность комнаты в DTO для передачи клиенту.
     * <p>
     * Маппит все поля сущности комнаты в соответствующие поля DTO.
     * </p>
     *
     * @param room сущность комнаты из базы данных
     * @return DTO комнаты для отправки клиенту
     */
    RoomResponse toDto(RoomEntity room);
}
