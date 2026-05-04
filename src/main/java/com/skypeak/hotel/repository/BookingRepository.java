package com.skypeak.hotel.repository;

import com.skypeak.hotel.entity.BookingEntity;
import com.skypeak.hotel.entity.enums.BookingStatus;
import com.skypeak.hotel.service.BookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Репозиторий для доступа к данным о бронированиях ({@link BookingEntity}).
 * <p>
 * Предоставляет методы для поиска бронирований пользователей и проверки доступности комнат.
 * Обеспечивает бизнес-логику по предотвращению двойного бронирования.
 * </p>
 *
 * <p><strong>Особенности:</strong></p>
 * <ul>
 *   <li>Бронирования пользователей сортируются по дате создания (новые первыми)</li>
 *   <li>Методы проверки конфликтов бронирования для обеспечения целостности данных</li>
 *   <li>Поддержка пагинации для больших списков бронирований</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see BookingEntity
 * @see BookingStatus
 * @see BookingService
 */
@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, UUID> {

    /**
     * Находит все бронирования пользователя, отсортированные по дате создания (новые первыми).
     * <p>
     * Используется для отображения истории бронирований пользователя в личном кабинете.
     * </p>
     *
     * @param userId   UUID пользователя
     * @param pageable параметры пагинации (номер страницы, размер, сортировка)
     * @return {@link Page} с бронированиями пользователя
     */
    Page<BookingEntity> findByUser_IdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    /**
     * Проверяет наличие конфликтующих бронирований для комнаты в указанный период.
     * <p>
     * Ищет активные бронирования (не CANCELLED), которые пересекаются с запрашиваемым периодом.
     * Используется для предотвращения двойного бронирования одной комнаты.
     * </p>
     *
     * @param id           UUID комнаты для проверки
     * @param status       статус бронирования, который исключается из поиска (обычно CANCELLED)
     * @param checkOutDate дата выезда из запрашиваемого периода (меньше этой даты)
     * @param checkInDate  дата заезда в запрашиваемый период (больше этой даты)
     * @return {@code true}  если найдены конфликтующие бронирования
     */
    boolean existsByRoom_IdAndStatusNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            UUID id,
            BookingStatus status,
            LocalDate checkOutDate,
            LocalDate checkInDate);
}