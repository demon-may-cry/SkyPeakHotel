package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.room.RoomResponse;
import com.skypeak.hotel.mapper.RoomMapper;
import com.skypeak.hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Дмитрий Ельцов
 */
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final RoomMapper roomMapper;

    @GetMapping()
    public Page<RoomResponse> getActiveRooms(Pageable pageable) {
        return roomService.getActiveRooms(pageable)
                .map(roomMapper::toDto);
    }

}

