package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.room.RoomResponse;
import com.skypeak.hotel.entity.RoomEntity;
import com.skypeak.hotel.mapper.RoomMapper;
import com.skypeak.hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

/**
 * Контроллер для управления комнатами.
 * <p>
 * Предоставляет эндпоинты для получения информации о доступных комнатах.
 * Доступен без ограничений по ролям.
 *
 * @author Дмитрий Ельцов
 * @see RoomService
 * @see RoomMapper
 */
@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;
    private final RoomMapper roomMapper;

    @GetMapping("/{roomId}")
    public RoomResponse getRoomById(@PathVariable UUID roomId) {
        log.info("▶️ Получен запрос на получение комнаты по ID: {}", roomId);

        RoomResponse response = roomMapper.toDto(roomService.getRoomById(roomId));

        log.info("✅ Успешно получена комната с ID: {}", roomId);
        return response;

    }

    /**
     * Возвращает пагинированный список активных комнат.
     *
     * @param pageable параметры пагинации (page, size, sort).
     * @return {@link Page} с {@link RoomResponse} активных комнат.
     */
    @GetMapping()
    public Page<RoomResponse> getActiveRooms(@ParameterObject
                                                 @PageableDefault(sort = "roomNumber") Pageable pageable) {
        log.info("▶️ Получен запрос на получение списка активных комнат. Параметры пагинации: {}", pageable);

        Page<RoomResponse> rooms = roomService.getActiveRooms(pageable)
                .map(roomMapper::toDto);

        log.info("✅ Успешно возвращен список из {} активных комнат на странице {}",
                rooms.getNumberOfElements(), rooms.getNumber());
        return rooms;
    }

}

