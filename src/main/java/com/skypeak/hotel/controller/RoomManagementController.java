package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.room.CreateRoomRequest;
import com.skypeak.hotel.dto.room.RoomResponse;
import com.skypeak.hotel.dto.room.UpdateRoomRequest;
import com.skypeak.hotel.mapper.RoomMapper;
import com.skypeak.hotel.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
@RestController
@RequestMapping("/api/management/rooms")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
public class RoomManagementController {

    private final RoomService roomService;
    private final RoomMapper roomMapper;

    @PostMapping
    public RoomResponse createRoom(@RequestBody @Valid CreateRoomRequest request) {

        var room = roomService.createRoom(request);

        return roomMapper.toDto(room);
    }

    @PutMapping("/{id}")
    public RoomResponse updateRoom(@PathVariable UUID id,
                                   @RequestBody @Valid UpdateRoomRequest request) {

        var room = roomService.updateRoom(id, request);

        return roomMapper.toDto(room);
    }

    @PatchMapping("/{id}/deactivate")
    public void deactivateRoom(@PathVariable UUID id) {
        roomService.deactivateRoom(id);
    }

}
