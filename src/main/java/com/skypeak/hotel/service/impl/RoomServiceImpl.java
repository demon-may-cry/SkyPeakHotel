package com.skypeak.hotel.service.impl;

import com.skypeak.hotel.dto.room.CreateRoomRequest;
import com.skypeak.hotel.dto.room.UpdateRoomRequest;
import com.skypeak.hotel.entity.RoomEntity;
import com.skypeak.hotel.entity.RoomTypeEntity;
import com.skypeak.hotel.repository.RoomRepository;
import com.skypeak.hotel.repository.RoomTypeEntityRepository;
import com.skypeak.hotel.service.RoomService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static java.text.MessageFormat.format;

/**
 * Реализация сервиса для управления номерами отеля.
 * <p>
 * Обеспечивает операции с номерами: получение активных номеров, создание,
 * обновление и деактивацию номеров. Включает помощник для расчета стоимости.
 * </p>
 *
 * @author Дмитрий Ельцов
 * @see RoomService
 */
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeEntityRepository roomTypeEntityRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public RoomEntity getRoomById(UUID roomId) {
        return roomRepository.findById(roomId).orElseThrow(() -> {
            log.warn("⚠️ Номер не найден. ID: {}", roomId);
            return new EntityNotFoundException(format("Номер с id {0} не найден", roomId));
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<RoomEntity> getActiveRooms(Pageable pageable) {
        log.info("▶️ Запрос на получение активных номеров. Pageable: {}", pageable);

        Page<RoomEntity> activeRooms = roomRepository.findByActiveTrue(pageable);

        log.info("✅ Активные номера получены. Количество: {}, Всего страниц: {}",
                activeRooms.getNumberOfElements(),
                activeRooms.getTotalPages());

        return activeRooms;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoomEntity createRoom(CreateRoomRequest request) {
        log.info("▶️ Запрос на создание номера. Номер: {}, Тип: {}", request.getRoomNumber(), request.getRoomType());

        // Проверка уникальности номера
        validateRoomNumberUniqueness(request.getRoomNumber());

        // Получение типа номера
        RoomTypeEntity roomType = getRoomTypeOrThrow(request.getRoomType());

        // Создание новой сущности номера
        RoomEntity room = new RoomEntity();
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(roomType);
        room.setActive(request.isActive());

        RoomEntity savedRoom = roomRepository.save(room);
        log.info("✅ Номер успешно создан. ID: {}, Номер: {}", savedRoom.getId(), savedRoom.getRoomNumber());

        return savedRoom;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoomEntity updateRoom(UUID roomId, UpdateRoomRequest request) {
        log.info("▶️ Запрос на обновление номера. ID: {}", roomId);

        // Получение существующего номера
        RoomEntity room = getRoomById(roomId);

        // Обновление номера комнаты
        if (request.getRoomNumber() != null && !request.getRoomNumber().equals(room.getRoomNumber())) {
            // Проверка уникальности нового номера (если он отличается от текущего)
            validateRoomNumberUniqueness(request.getRoomNumber());

            log.debug("Обновление номера с {} на {}", room.getRoomNumber(), request.getRoomNumber());
            room.setRoomNumber(request.getRoomNumber());
        }

        // Обновление типа номера
        if (request.getRoomType() != null && !request.getRoomType().toLowerCase().equals(room.getRoomType().getSlug())) {
            RoomTypeEntity roomType = getRoomTypeOrThrow(request.getRoomType());

            log.debug("Обновление типа комнаты с {} на {}", room.getRoomType().getSlug(), request.getRoomType());
            room.setRoomType(roomType);
        }

        // Обновление статуса активности
        if (request.getActive() != null && request.getActive() != room.isActive()) {
            log.debug("Обновление статуса активности с {} на {}", room.isActive(), request.getActive());
            room.setActive(request.getActive());
        }

        RoomEntity updatedRoom = roomRepository.save(room);
        log.info("✅ Номер успешно обновлен. ID: {}, Номер: {}", updatedRoom.getId(), updatedRoom.getRoomNumber());

        return updatedRoom;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivateRoom(UUID roomId) {
        log.info("▶️ Запрос на деактивацию номера. ID: {}", roomId);

        // Получение номера
        RoomEntity room = getRoomById(roomId);

        // Проверка уже деактивирован ли номер
        if (!room.isActive()) {
            log.warn("⚠️ Номер уже деактивирован. ID: {}", roomId);
            throw new IllegalStateException("Номер уже деактивирован.");
        }

        // Деактивация номера
        room.setActive(false);
        roomRepository.save(room);
        log.info("✅ Номер успешно деактивирован. ID: {}, Номер: {}", roomId, room.getRoomNumber());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculatePriceForDays(RoomEntity room, long nights) {
        log.info("▶️ Расчет стоимости. Номер: {}, Кол-во ночей: {}", room.getRoomNumber(), nights);

        RoomTypeEntity roomType = room.getRoomType();
        BigDecimal basePrice = roomType.getBasePrice();

        log.debug("💰 Базовая цена за ночь: {}, Тип номера: {}", basePrice, roomType.getSlug());

        // Расчет общей стоимости
        BigDecimal totalPrice = basePrice.multiply(BigDecimal.valueOf(nights));
        log.info("✅ Итоговая стоимость рассчитана: {} ({} * {})", totalPrice, basePrice, nights);

        return totalPrice;
    }

    /**
     * Получает тип номера по slug.
     * <p>
     * Вспомогательный метод для валидации и получения типа номера.
     * </p>
     *
     * @param roomTypeSlug slug типа номера
     * @return сущность {@link RoomTypeEntity}
     * @throws EntityNotFoundException если тип не найден
     */
    private RoomTypeEntity getRoomTypeOrThrow(String roomTypeSlug) {
        return roomTypeEntityRepository.findBySlug(roomTypeSlug.toLowerCase()).orElseThrow(() -> {
            log.warn("⚠️ Тип комнаты не найден. Тип: {}", roomTypeSlug);
            return new EntityNotFoundException("Тип комнаты не найден.");
        });
    }

    /**
     * Проверяет уникальность номера комнаты.
     * <p>
     * Вспомогательный метод для валидации уникальности номера.
     * Если номер уже существует, выбрасывает исключение.
     * </p>
     *
     * @param roomNumber номер комнаты для проверки
     * @throws IllegalStateException если номер уже существует
     */
    private void validateRoomNumberUniqueness(String roomNumber) {
        if (roomRepository.existsByRoomNumber(roomNumber)) {
            log.warn("⚠️ Номер с таким номером уже существует. Номер: {}", roomNumber);
            throw new IllegalStateException("Номер с таким номером уже существует.");
        }
    }
}
