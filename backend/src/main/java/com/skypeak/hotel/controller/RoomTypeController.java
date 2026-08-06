package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.roomtype.RoomTypeResponse;
import com.skypeak.hotel.mapper.RoomTypeMapper;
import com.skypeak.hotel.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер для получения информации о типах комнат.
 * <p>
 * Предоставляет эндпоинты для просмотра всех доступных категорий номеров
 * и получения детальной информации по конкретному типу через его slug.
 *
 * @author Дмитрий Ельцов
 * @see RoomTypeService
 * @see RoomTypeMapper
 */
@RestController
@RequestMapping("/api/v1/room-types")
@RequiredArgsConstructor
@Slf4j
public class RoomTypeController {

    private final RoomTypeService roomTypeService;
    private final RoomTypeMapper roomTypeMapper;

    /**
     * Возвращает список всех доступных типов номеров.
     *
     * @return список {@link RoomTypeResponse} с информацией о типах комнат.
     */
    @GetMapping
    public List<RoomTypeResponse> getAll() {
        log.info("▶️ Получен запрос на получение всех типов комнат");

        List<RoomTypeResponse> response = roomTypeService.getAllRoomTypes()
                .stream()
                .map(roomTypeMapper::toDto)
                .toList();

        log.info("✅ Список типов комнат успешно получен. Количество: {}", response.size());
        return response;
    }

    /**
     * Возвращает информацию о типе комнаты по его slug.
     *
     * @param slug строковый идентификатор типа комнаты.
     * @return {@link RoomTypeResponse} с данными типа комнаты.
     * @throws jakarta.persistence.EntityNotFoundException если тип комнаты не найден.
     */
    @GetMapping("/{slug}")
    public RoomTypeResponse getBySlug(@PathVariable String slug) {
        log.info("▶️ Получен запрос на получение типа комнаты по slug: {}", slug);

        RoomTypeResponse response = roomTypeMapper.toDto(roomTypeService.getBySlug(slug));

        log.info("✅ Тип комнаты успешно найден: {}", response.slug());
        return response;
    }
}
