package com.skypeak.hotel.service;

import com.skypeak.hotel.dto.room.CreateRoomRequest;
import com.skypeak.hotel.dto.room.UpdateRoomRequest;
import com.skypeak.hotel.entity.RoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
public interface RoomService {

    Page<RoomEntity> getActiveRooms(Pageable pageable);

    RoomEntity createRoom(CreateRoomRequest request);

    RoomEntity updateRoom(UUID roomId, UpdateRoomRequest request);

    void deactivateRoom(UUID roomId);

    /**
     * Рассчитывает стоимость проживания в комнате за указанное количество дней.
     *
     * @param room  сущность комнаты с ценой за ночь
     * @param days  количество дней проживания
     * @return общая стоимость за указанный период
     */
    BigDecimal calculatePriceForDays(RoomEntity room, int days);
}
