package com.skypeak.hotel.service;

import com.skypeak.hotel.dto.room.CreateRoomRequest;
import com.skypeak.hotel.dto.room.UpdateRoomRequest;
import com.skypeak.hotel.entity.RoomEntity;

import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
public interface RoomService {

    RoomEntity createRoom(CreateRoomRequest request);

    RoomEntity updateRoom(UUID roomId, UpdateRoomRequest request);

    void deactivateRoom(UUID roomId);
}
