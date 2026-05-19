package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.room.CreateRoomRequest;
import com.skypeak.hotel.dto.room.RoomResponse;
import com.skypeak.hotel.dto.room.UpdateRoomRequest;
import com.skypeak.hotel.entity.RoomEntity;
import com.skypeak.hotel.mapper.RoomMapper;
import com.skypeak.hotel.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Контроллер для управления комнатами отеля.
 * <p>
 * Предоставляет эндпоинты для создания, обновления и деактивации комнат.
 * Доступ к эндпоинтам ограничен ролями MANAGER и ADMIN.
 *
 * @author Дмитрий Ельцов
 * @see RoomService
 * @see RoomMapper
 * @see PreAuthorize
 */
@RestController
@RequestMapping("/api/v1/management/rooms")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
@Slf4j
public class RoomManagementController {

    private final RoomService roomService;
    private final RoomMapper roomMapper;

    /**
     * Создает новую комнату в отеле.
     *
     * @param request DTO с информацией о новой комнате (тип, цена, описание и т.д.).
     * @return {@link RoomResponse} с данными созданной комнаты.
     * @throws IllegalArgumentException если данные комнаты не валидны.
     */
    @PostMapping
    public RoomResponse createRoom(@RequestBody @Valid CreateRoomRequest request) {
        log.info("▶️ Получен запрос на создание новой комнаты. Номер: {}, Тип: {}",
                request.getRoomNumber(), request.getRoomType());

        RoomEntity room = roomService.createRoom(request);

        RoomResponse response = roomMapper.toDto(room);
        log.info("✅ Комната успешно создана. ID: {}, Номер: {}, Тип: {}",
                response.id(), response.roomNumber(), response.roomType());
        return response;
    }

    /**
     * Обновляет информацию о существующей комнате.
     *
     * @param id      UUID комнаты для обновления.
     * @param request DTO с новыми данными комнаты.
     * @return {@link RoomResponse} с обновленными данными комнаты.
     * @throws jakarta.persistence.EntityNotFoundException если комната не найдена.
     * @throws IllegalArgumentException если данные комнаты не валидны.
     */
    @PutMapping("/{id}")
    public RoomResponse updateRoom(@PathVariable UUID id,
                                   @RequestBody @Valid UpdateRoomRequest request) {
        log.info("▶️ Получен запрос на обновление комнаты {}. Новый тип: {}",
                id, request.getRoomType());

        RoomEntity room = roomService.updateRoom(id, request);

        RoomResponse response = roomMapper.toDto(room);
        log.info("✅ Комната {} успешно обновлена. Новый номер: {}, Новый тип: {}",
                id, response.roomNumber(), response.roomType());
        return response;
    }

    /**
     * Деактивирует комнату, делая её недоступной для бронирования.
     *
     * @param id UUID комнаты для деактивации.
     * @throws jakarta.persistence.EntityNotFoundException если комната не найдена.
     * @throws IllegalArgumentException если комната уже деактивирована.
     */
    @PatchMapping("/{id}/deactivate")
    public void deactivateRoom(@PathVariable UUID id) {
        log.info("▶️ Получен запрос на деактивацию комнаты: {}", id);

        roomService.deactivateRoom(id);

        log.info("✅ Комната {} успешно деактивирована", id);
    }

}
