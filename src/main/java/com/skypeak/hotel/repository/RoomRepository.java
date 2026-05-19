package com.skypeak.hotel.repository;

import com.skypeak.hotel.entity.RoomEntity;
import com.skypeak.hotel.service.RoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Репозиторий для доступа к данным о номерах отеля ({@link RoomEntity}).
 * <p>
 * Предоставляет методы для поиска активных номеров и проверки уникальности номеров комнат.
 * Обеспечивает бизнес-логику по управлению доступностью номеров.
 * </p>
 *
 * <p><strong>Особенности:</strong></p>
 * <ul>
 *   <li>Только активные номера (active = true) доступны для бронирования</li>
 *   <li>Номера комнат должны быть уникальными в рамках отеля</li>
 *   <li>Поддержка пагинации для больших списков номеров</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see RoomEntity
 * @see RoomService
 */
@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, UUID> {

    /**
     * Находит все активные номера отеля с пагинацией.
     * <p>
     * Возвращает только номера, доступные для бронирования (active = true).
     * Используется для отображения списка доступных номеров клиентам.
     * </p>
     *
     * @param pageable параметры пагинации (номер страницы, размер, сортировка)
     * @return {@link Page} с активными номерами
     */
    @EntityGraph(attributePaths = "roomType")
    Page<RoomEntity> findByActiveTrue(Pageable pageable);

    /**
     * Проверяет существование номера комнаты с указанным номером.
     * <p>
     * Используется для валидации уникальности номера при создании нового номера
     * или изменении существующего.
     * </p>
     *
     * @param roomNumber номер комнаты для проверки
     * @return {@code true} если номер комнаты уже существует
     */
    boolean existsByRoomNumber(String roomNumber);
}