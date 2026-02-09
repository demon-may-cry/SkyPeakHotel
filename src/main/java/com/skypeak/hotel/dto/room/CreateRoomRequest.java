package com.skypeak.hotel.dto.room;

import com.skypeak.hotel.entity.enums.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author Дмитрий Ельцов
 */
@Getter
@Setter
public class CreateRoomRequest {

    @NotNull
    @Size(max = 20)
    private String roomNumber;

    @NotNull
    private RoomType roomType;

    @NotNull
    private BigDecimal pricePerNight;

    @Size(max = 255)
    private String description;
}
