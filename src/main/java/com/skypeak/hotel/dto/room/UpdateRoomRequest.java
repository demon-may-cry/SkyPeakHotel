package com.skypeak.hotel.dto.room;

import com.skypeak.hotel.entity.enums.RoomType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author Дмитрий Ельцов
 */
@Getter
@Setter
public class UpdateRoomRequest {

    private RoomType roomType;

    private BigDecimal pricePerNight;

    private String description;

    private Boolean active;
}
