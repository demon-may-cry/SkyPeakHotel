package com.skypeak.hotel.mapper;

import com.skypeak.hotel.dto.roomtype.RoomTypeResponse;
import com.skypeak.hotel.entity.RoomTypeEntity;
import com.skypeak.hotel.mapper.config.CentralMapperConfig;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;

/**
 * Маппер для преобразования между RoomTypeEntity и RoomTypeResponse DTO.
 * <p>
 * Отвечает за конвертацию полей сущности типа комнаты в Data Transfer Object.
 * Использует MapStruct для автоматической генерации реализации.
 * </p>
 *
 * @author Дмитрий Ельцов
 * @see RoomTypeEntity
 * @see RoomTypeResponse
 * @see CentralMapperConfig
 */
@Mapper(config = CentralMapperConfig.class)
public interface RoomTypeMapper {

    /**
     * Преобразует сущность типа комнаты в DTO для передачи клиенту.
     *
     * @param roomType сущность типа комнаты из базы данных
     * @return DTO типа комнаты
     */
    @BeanMapping(ignoreUnmappedSourceProperties = {
            "rooms"
    })
    RoomTypeResponse toDto(RoomTypeEntity roomType);
}
