package com.skypeak.hotel.service.impl;

import com.skypeak.hotel.dto.room.CreateRoomRequest;
import com.skypeak.hotel.dto.room.UpdateRoomRequest;
import com.skypeak.hotel.entity.RoomEntity;
import com.skypeak.hotel.repository.RoomRepository;
import com.skypeak.hotel.service.RoomService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    public RoomEntity createRoom(CreateRoomRequest request) {
//TODO: Could not commit JPA transaction
        if (roomRepository.existsByRoomNumber(request.getRoomNumber()))
            throw new IllegalStateException("Room with this number already exists");
        log.info("Creating room with number: {}", request.getRoomNumber());

        RoomEntity room = new RoomEntity();
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(request.getRoomType());
        room.setPricePerNight(request.getPricePerNight());
        room.setDescription(request.getDescription());
        room.setActive(true);

        log.info("Room created with number: {}", room.getRoomNumber());
        return roomRepository.save(room);
    }

    @Override
    public RoomEntity updateRoom(UUID roomId, UpdateRoomRequest request) {

        var room = roomRepository.findById(roomId).orElseThrow(() ->
                new EntityNotFoundException("Room not found"));

        if (request.getRoomType() != null) room.setRoomType(request.getRoomType());
        if (request.getPricePerNight() != null) room.setPricePerNight(request.getPricePerNight());
        if (request.getDescription() != null) room.setDescription(request.getDescription());
        if (request.getActive() != null) room.setActive(request.getActive());

        return roomRepository.save(room);
    }

    @Override
    public void deactivateRoom(UUID roomId) {

        var room = roomRepository.findById(roomId).orElseThrow(() ->
                new EntityNotFoundException("Room not found"));

        if (!room.isActive()) throw new IllegalStateException("Room is already deactivated");
        room.setActive(false);

        roomRepository.save(room);
    }
}
