package com.skypeak.hotel.mapper;

import com.skypeak.hotel.dto.booking.BookingResponse;
import com.skypeak.hotel.entity.BookingEntity;
import com.skypeak.hotel.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Дмитрий Ельцов
 */
@Mapper(config = CentralMapperConfig.class)
public interface BookingMapper {

    @Mapping(source = "room.id", target = "roomId")
    BookingResponse toDto(BookingEntity booking);
}
