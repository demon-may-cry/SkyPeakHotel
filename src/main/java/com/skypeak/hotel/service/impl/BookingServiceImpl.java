package com.skypeak.hotel.service.impl;

import com.skypeak.hotel.entity.BookingEntity;
import com.skypeak.hotel.entity.RoomEntity;
import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.entity.enums.BookingStatus;
import com.skypeak.hotel.repository.BookingRepository;
import com.skypeak.hotel.service.BalanceService;
import com.skypeak.hotel.service.BookingService;
import com.skypeak.hotel.service.RoomService;
import com.skypeak.hotel.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static java.text.MessageFormat.format;

/**
 * Реализация сервиса для управления бронированиями.
 * <p>
 * Обеспечивает создание, отмену и просмотр бронирований. Все операции выполняются
 * в контексте транзакции для обеспечения консистентности данных и атомарности платежей.
 * </p>
 *
 * <h3>Особенности реализации:</h3>
 * <ul>
 *   <li>Все операции транзакционны для обеспечения целостности данных</li>
 *   <li>Валидация выполняется ДО финансовых операций</li>
 *   <li>Полный контроль ошибок с детальным логированием</li>
 *   <li>Защита от попыток отмены чужих бронирований</li>
 *   <li>Автоматический расчет стоимости и возврата средств</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see BookingService
 * @see BookingEntity
 * @see RoomEntity
 */
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BalanceService balanceService;
    private final RoomService roomService;
    private final UserService userService;

    /**
     * {@inheritDoc}
     */
    @Override
    public BookingEntity createBooking(UUID userId, UUID roomId, LocalDate checkIn, LocalDate checkOut) {
        log.info("▶️ Запрос на создание бронирования. Пользователь: {}, Номер комнаты: {}, Заезд: {}, Выезд: {}",
                userId, roomId, checkIn, checkOut);

        // ===== ЭТАП 1: ВАЛИДАЦИЯ (ДО финансовых операций) =====

        // Валидация дат
        validateBookingDates(checkIn, checkOut);

        // Проверка пользователя
        UserEntity user = userService.getUserById(userId);

        if (!user.isActive()) {
            log.warn("⚠️ Попытка бронирования неактивным пользователем. Пользователь ID: {}", userId);
            throw new IllegalStateException("Ваш аккаунт неактивен.");
        }

        // Проверка комнаты
        RoomEntity room = roomService.getRoomById(roomId);

        if (!room.isActive()) {
            log.warn("⚠️ Комната неактивна. Комната ID: {}", roomId);
            throw new IllegalStateException("Комната неактивна.");
        }

        // Проверка доступности на период
        validateRoomAvailability(roomId, checkIn, checkOut);

        // Расчет стоимости
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal totalCost = roomService.calculatePriceForDays(room, (int) nights);
        log.info("💰 Рассчитана стоимость бронирования. Ночей: {}, Сумма: {}", nights, totalCost);

        // Проверка достаточности средств
        validateUserBalance(user, totalCost);

        // ===== ЭТАП 2: ФИНАНСОВЫЕ ОПЕРАЦИИ =====

        log.debug("💳 Начисление платежа пользователю: {}", userId);

        String paymentDescription = buildPaymentDescription(room.getRoomNumber(), checkIn, checkOut);
        balanceService.withdraw(userId, totalCost, paymentDescription);

        // ===== ЭТАП 3: СОЗДАНИЕ БРОНИРОВАНИЯ =====

        BookingEntity booking = new BookingEntity();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setStatus(BookingStatus.CREATED);
        booking.setCreatedAt(LocalDateTime.now());

        BookingEntity savedBooking = bookingRepository.save(booking);
        log.info("✅ Бронирование успешно создано. ID: {}, Комната: {}, Пользователь: {}, Стоимость: {}",
                savedBooking.getId(), room.getRoomNumber(), userId, totalCost);

        return savedBooking;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelBooking(UUID bookingId, UUID userId) {
        log.info("▶️ Запрос на отмену бронирования. ID: {}, Пользователь ID: {}", bookingId, userId);

        // Получение бронирования
        BookingEntity booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.warn("⚠️ Бронирование не найдено. ID: {}", bookingId);
            return new EntityNotFoundException("Бронирование не найдено.");
        });

        // Проверка прав владельца
        if (!booking.getUser().getId().equals(userId)) {
            log.warn("🚫 Попытка отмены чужого бронирования. ID: {}, Владелец: {}, Заказчик: {}",
                    bookingId, booking.getUser().getId(), userId);
            throw new SecurityException("Вы не имеете права отменять это бронирование");
        }

        // Проверка статуса бронирования
        validateBookingStatusForCancellation(booking);

        // Расчет суммы возврата
        long nights = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        BigDecimal refund = roomService.calculatePriceForDays(booking.getRoom(), (int) nights);
        log.info("💰 Рассчитана сумма возврата. Ночей: {}, Сумма: {}", nights, refund);

        // Выполнение возврата
        log.debug("💳 Возврат средств пользователю: {}", userId);

        String refundDescription = buildRefundDescription(booking.getRoom().getRoomNumber(),
                booking.getCheckInDate(), booking.getCheckOutDate());
        balanceService.deposit(userId, refund, refundDescription);

        // Сохранение статуса отмены
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        log.info("✅ Бронирование успешно отменено. ID: {}, Возврат: {}", bookingId, refund);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<BookingEntity> getUserBookings(UUID userId, Pageable pageable) {
        log.info("▶️ Запрос на получение бронирований пользователя. Пользователь ID: {}, Всего страниц: {}", userId, pageable);

        // Проверка существования пользователя
        userService.getUserById(userId);

        Page<BookingEntity> bookings = bookingRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable);
        log.info("✅ Бронирования получены. Количество: {}, Всего страниц: {}",
                bookings.getNumberOfElements(), bookings.getTotalPages());

        return bookings;
    }

    // ===== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ =====

    /**
     * Валидирует даты бронирования.
     *
     * @param checkIn дата заезда
     * @param checkOut дата выезда
     * @throws IllegalArgumentException если даты некорректны
     */
    private void validateBookingDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            log.warn("⚠️ Даты бронирования не установлены. Заезд: {}, Выезд: {}", checkIn, checkOut);
            throw new IllegalArgumentException("Даты заезда и выезда обязательны.");
        }

        if (!checkIn.isBefore(checkOut)) {
            log.warn("⚠️ Некорректные даты бронирования. Заезд: {}, Выезд: {}", checkIn, checkOut);
            throw new IllegalArgumentException("Дата заезда должна быть раньше даты выезда.");
        }
    }

    /**
     * Проверяет доступность комнаты на указанный период.
     *
     * @param roomId ID комнаты
     * @param checkIn дата заезда
     * @param checkOut дата выезда
     * @throws IllegalStateException если комната уже забронирована
     */
    private void validateRoomAvailability(UUID roomId, LocalDate checkIn, LocalDate checkOut) {
        boolean bookingExists = bookingRepository.existsByRoom_IdAndStatusNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                roomId,
                BookingStatus.CANCELLED,
                checkOut,
                checkIn
        );

        if (bookingExists) {
            log.warn("⚠️ Комната забронирована на указанный период. Комната ID: {}, Заезд: {}, Выезд: {}",
                    roomId, checkIn, checkOut);
            throw new IllegalStateException("Комната уже забронирована на указанный период.");
        }
    }

    /**
     * Проверяет достаточность средств у пользователя.
     *
     * @param user пользователь
     * @param amount требуемая сумма
     * @throws IllegalStateException если средств недостаточно
     */
    private void validateUserBalance(UserEntity user, BigDecimal amount) {
        BigDecimal balance = balanceService.getBalance(user.getId());
        if (balance.compareTo(amount) < 0) {
            log.warn("⚠️ Недостаточно средств. Пользователь ID: {}, Необходимо: {}, Доступно: {}",
                    user.getId(), amount, balance);
            throw new IllegalStateException("Недостаточно средств на счете.");
        }
    }

    /**
     * Проверяет возможность отмены бронирования.
     *
     * @param booking бронирование для проверки
     * @throws IllegalStateException если отмена невозможна
     */
    private void validateBookingStatusForCancellation(BookingEntity booking) {
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            log.warn("⚠️ Бронирование уже отменено. ID: {}", booking.getId());
            throw new IllegalStateException("Бронирование уже отменено.");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            log.warn("⚠️ Невозможно отменить завершенное бронирование. ID: {}", booking.getId());
            throw new IllegalStateException("Невозможно отменить завершенное бронирование.");
        }
    }

    /**
     * Формирует описание платежа для бронирования.
     *
     * @param roomNumber номер комнаты
     * @param checkIn дата заезда
     * @param checkOut дата выезда
     * @return строка описания платежа
     */
    private String buildPaymentDescription(String roomNumber, LocalDate checkIn, LocalDate checkOut) {
        return format("Оплата за бронирование номера {0} с {1} по {2}", roomNumber, checkIn, checkOut);
    }

    /**
     * Формирует описание возврата средств.
     *
     * @param roomNumber номер комнаты
     * @param checkIn дата заезда
     * @param checkOut дата выезда
     * @return строка описания возврата
     */
    private String buildRefundDescription(String roomNumber, LocalDate checkIn, LocalDate checkOut) {
        return format("Возврат за отмену бронирования номера {0} с {1} по {2}", roomNumber, checkIn, checkOut);
    }
}