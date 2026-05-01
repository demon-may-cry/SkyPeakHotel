package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.room.RoomResponse;
import com.skypeak.hotel.mapper.RoomMapper;
import com.skypeak.hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

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

    /**
     * Возвращает пагинированный список активных комнат.
     *
     * @param pageable параметры пагинации (page, size, sort).
     * @return {@link Page} с {@link RoomResponse} активных комнат.
     */
    @GetMapping()
    public Page<RoomResponse> getActiveRooms(Pageable pageable) {
        log.info("▶️ Получен запрос на получение списка активных комнат. Параметры пагинации: {}", pageable);

        Page<RoomResponse> rooms = roomService.getActiveRooms(pageable)
                .map(roomMapper::toDto);

        log.info("✅ Успешно возвращен список из {} активных комнат на странице {}",
                rooms.getNumberOfElements(), rooms.getNumber());
        return rooms;
    }

}

