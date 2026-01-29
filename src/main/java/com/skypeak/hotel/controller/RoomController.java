package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.RoomResponse;
import com.skypeak.hotel.entity.RoomEntity;
import com.skypeak.hotel.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author Дмитрий Ельцов
 */
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomRepository roomRepository;

    @GetMapping()
    private Page<RoomResponse> getActiveRooms(Pageable pageable) {
        return roomRepository.findByActiveTrue(pageable)
                .map(this::toDto);
    }

    private RoomResponse toDto(RoomEntity room) {
        return new RoomResponse(
                room.getId(),
                room.getRoomNumber(),
                room.getRoomType(),
                room.getPricePerNight(),
                room.isActive(),
                room.getDescription()
        );
    }
}

