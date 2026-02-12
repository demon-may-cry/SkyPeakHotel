package com.skypeak.hotel.mapper;

import com.skypeak.hotel.dto.room.RoomResponse;
import com.skypeak.hotel.entity.RoomEntity;
import com.skypeak.hotel.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;

/**
 * @author Дмитрий Ельцов
 */
@Mapper(config = CentralMapperConfig.class)
public interface RoomMapper {

    RoomResponse toDto(RoomEntity room);
}
