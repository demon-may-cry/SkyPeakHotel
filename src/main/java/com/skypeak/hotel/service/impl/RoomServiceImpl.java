package com.skypeak.hotel.service.impl;

import com.skypeak.hotel.dto.room.CreateRoomRequest;
import com.skypeak.hotel.dto.room.UpdateRoomRequest;
import com.skypeak.hotel.entity.RoomEntity;
import com.skypeak.hotel.repository.RoomRepository;
import com.skypeak.hotel.service.RoomService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
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

    @Override
    public RoomEntity getRoomById(UUID roomId) {
        return roomRepository.findById(roomId).orElseThrow(() ->
                new EntityNotFoundException(format("Комната с id {0} не найдена", roomId)));
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
                activeRooms.getNumberOfElements(), activeRooms.getTotalPages());
        return activeRooms;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoomEntity createRoom(CreateRoomRequest request) {
        log.info("▶️ Запрос на создание номера. Номер: {}, Тип: {}, Цена: {}",
                request.getRoomNumber(), request.getRoomType(), request.getPricePerNight());

        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            log.warn("⚠️ Номер с таким номером уже существует. Номер: {}", request.getRoomNumber());
            throw new IllegalStateException("Номер с таким номером уже существует.");
        }

        RoomEntity room = new RoomEntity();
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(request.getRoomType());
        room.setPricePerNight(request.getPricePerNight());
        room.setDescription(request.getDescription());
        room.setActive(true);

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

        var room = roomRepository.findById(roomId).orElseThrow(() -> {
            log.warn("⚠️ Номер не найден. ID: {}", roomId);
            return new EntityNotFoundException("Room not found");
        });

        if (request.getRoomType() != null) {
            log.debug("Обновление типа комнаты с {} на {}", room.getRoomType(), request.getRoomType());
            room.setRoomType(request.getRoomType());
        }
        if (request.getPricePerNight() != null) {
            log.debug("Обновление цены с {} на {}", room.getPricePerNight(), request.getPricePerNight());
            room.setPricePerNight(request.getPricePerNight());
        }
        if (request.getDescription() != null) {
            log.debug("Обновление описания");
            room.setDescription(request.getDescription());
        }
        if (request.getActive() != null) {
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

        var room = roomRepository.findById(roomId).orElseThrow(() -> {
            log.warn("⚠️ Номер не найден. ID: {}", roomId);
            return new EntityNotFoundException("Номер не найден.");
        });

        if (!room.isActive()) {
            log.warn("⚠️ Номер уже деактивирован. ID: {}", roomId);
            throw new IllegalStateException("Номер уже деактивирован.");
        }

        room.setActive(false);
        roomRepository.save(room);
        log.info("✅ Номер успешно деактивирован. ID: {}, Номер: {}", roomId, room.getRoomNumber());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal calculatePriceForDays(RoomEntity room, int days) {
        log.info("▶️ Расчет стоимости. Номер: {}, Цена за ночь: {}, Дней: {}",
                room.getRoomNumber(), room.getPricePerNight(), days);
        BigDecimal price = room.getPricePerNight().multiply(BigDecimal.valueOf(days));
        log.info("✅ Итоговая стоимость: {}", price);
        return price;
    }
}
