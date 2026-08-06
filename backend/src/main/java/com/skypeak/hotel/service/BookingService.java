package com.skypeak.hotel.service;

import com.skypeak.hotel.dto.booking.BookingRequest;
import com.skypeak.hotel.dto.booking.BookingResponse;
import com.skypeak.hotel.entity.BookingEntity;
import com.skypeak.hotel.service.impl.BookingServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Сервис для управления бронированиями номеров отеля.
 * <p>
 * Определяет контракт для выполнения операций с бронированиями,
 * таких как создание, отмена и просмотр истории бронирований пользователя.
 * </p>
 *
 * <h3>Особенности управления бронированиями:</h3>
 * <ul>
 *   <li>Бронирования привязаны к пользователю и комнате</li>
 *   <li>Предотвращение двойного бронирования одной комнаты на одинаковый период</li>
 *   <li>Поддержка различных статусов бронирования (CREATED, CANCELLED, COMPLETED)</li>
 *   <li>История всех бронирований пользователя сохраняется для аудита</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see BookingEntity
 * @see BookingServiceImpl
 * @see RoomService
 */
public interface BookingService {

    /**
     * Создает новое бронирование для пользователя на указанную комнату.
     * <p>
     * Проверяет доступность комнаты в указанный период, спишет средства со счета пользователя
     * и создает запись о бронировании со статусом CREATED.
     * </p>
     *
     * @param email email пользователя, совершающего бронирование
     * @param request данные для создания бронирования
     * @return возвращаемый ответ {@link BookingResponse} со статусом PENDING
     * @throws jakarta.persistence.EntityNotFoundException если пользователь или комната не найдены
     * @throws IllegalArgumentException                      если комната недоступна на указанный период или недостаточно средств
     * @throws IllegalStateException                         если даты бронирования некорректны (checkOut &lt;= checkIn)
     */
    BookingResponse createBooking(String email, BookingRequest request);

    /**
     * Отменяет существующее бронирование пользователя.
     * <p>
     * Устанавливает статус бронирования на CANCELLED и возвращает средства на счет пользователя.
     * Бронирование можно отменить только если оно еще не началось (checkIn в будущем).
     * </p>
     *
     * @param email email пользователя (для проверки прав собственности)
     * @param id UUID бронирования для отмены
     * @throws jakarta.persistence.EntityNotFoundException если бронирование или пользователь не найдены
     * @throws IllegalArgumentException                      если бронирование принадлежит другому пользователю
     * @throws IllegalStateException                         если бронирование уже отменено или уже началось
     */
    void cancelBooking(String email, UUID id);

    /**
     * Возвращает пагинированный список бронирований пользователя.
     * <p>
     * Бронирования возвращаются в порядке убывания даты создания (новые первыми).
     * </p>
     *
     * @param email email пользователя
     * @param pageable параметры пагинации (номер страницы, размер, сортировка)
     * @return {@link Page} с бронированиями пользователя
     */
    Page<BookingResponse> getUserBookings(String email, Pageable pageable);

    /**
     *
     * @param email
     * @param id
     * @return
     */
    BookingResponse payBooking(String email, UUID id);
}
