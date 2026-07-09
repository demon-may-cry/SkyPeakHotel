package com.skypeak.hotel.service;

import com.skypeak.hotel.dto.room.CreateRoomRequest;
import com.skypeak.hotel.dto.room.UpdateRoomRequest;
import com.skypeak.hotel.entity.RoomEntity;
import com.skypeak.hotel.service.impl.RoomServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Сервис для управления номерами отеля.
 * <p>
 * Определяет контракт для выполнения операций с номерами, таких как просмотр,
 * создание, обновление и деактивация номеров.
 * </p>
 *
 * <h3>Особенности управления номерами:</h3>
 * <ul>
 *   <li>Номера имеют типы (STANDARD, APARTMENTS, SUITE) с разными ценами</li>
 *   <li>Активные номера доступны для бронирования</li>
 *   <li>Номера могут быть деактивированы (не удаляются, а скрываются)</li>
 *   <li>Каждый номер имеет уникальный номер комнаты в пределах отеля</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see RoomEntity
 * @see RoomServiceImpl
 * @see BookingService
 */
public interface RoomService {

    /**
     * Получает номер по его идентификатору.
     * <p>
     * Вспомогательный метод для получения существующего номера.
     * Если номер не найден, выбрасывает исключение EntityNotFoundException.
     * </p>
     *
     * @param roomId UUID идентификатор номера
     * @return найденная сущность {@link RoomEntity}
     * @throws EntityNotFoundException если номер не найден в БД
     */
    RoomEntity getRoomById(UUID roomId);

    /**
     * Возвращает пагинированный список активных номеров, доступных для бронирования.
     * <p>
     * Возвращаются только номера со статусом active = true.
     * </p>
     *
     * @param pageable параметры пагинации (номер страницы, размер, сортировка)
     * @return {@link Page} с активными номерами
     */
    Page<RoomEntity> getActiveRooms(Pageable pageable);

    /**
     * Создает новый номер в отеле.
     * <p>
     * По умолчанию новый номер создается в статусе active = true.
     * Проверяет уникальность номера комнаты перед созданием.
     * </p>
     *
     * @param request DTO с данными нового номера (номер, тип, цена)
     * @return созданная сущность {@link RoomEntity}
     * @throws IllegalArgumentException если номер комнаты уже существует
     */
    RoomEntity createRoom(CreateRoomRequest request);

    /**
     * Обновляет информацию о существующем номере.
     * <p>
     * Позволяет изменять тип, цену и другие параметры номера.
     * </p>
     *
     * @param roomId  UUID номера для обновления
     * @param request DTO с новыми данными номера
     * @return обновленная сущность {@link RoomEntity}
     * @throws jakarta.persistence.EntityNotFoundException если номер не найден
     */
    RoomEntity updateRoom(UUID roomId, UpdateRoomRequest request);

    /**
     * Деактивирует номер, делая его недоступным для новых бронирований.
     * <p>
     * Номер не удаляется из БД, но становится невидимым для клиентов.
     * Существующие бронирования номера не отменяются.
     * </p>
     *
     * @param roomId UUID номера для деактивации
     * @throws jakarta.persistence.EntityNotFoundException если номер не найден
     */
    void deactivateRoom(UUID roomId);

    /**
     * Рассчитывает стоимость проживания в комнате за указанное количество ночей.
     * <p>
     * Формула: цена за ночь × количество дней
     * </p>
     *
     * @param room сущность комнаты с ценой за ночь
     * @param nights количество ночей
     * @return {@code BigDecimal} общая стоимость за указанный период
     */
    BigDecimal calculatePriceForDays(RoomEntity room, long nights);
}
