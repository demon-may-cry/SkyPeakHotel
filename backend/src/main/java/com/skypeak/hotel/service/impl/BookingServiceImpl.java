package com.skypeak.hotel.service.impl;

import com.skypeak.hotel.dto.booking.BookingRequest;
import com.skypeak.hotel.dto.booking.BookingResponse;
import com.skypeak.hotel.entity.BookingEntity;
import com.skypeak.hotel.entity.RoomEntity;
import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.mapper.BookingMapper;
import com.skypeak.hotel.repository.BookingRepository;
import com.skypeak.hotel.repository.RoomRepository;
import com.skypeak.hotel.service.BalanceService;
import com.skypeak.hotel.service.BookingService;
import com.skypeak.hotel.service.RoomService;
import com.skypeak.hotel.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static com.skypeak.hotel.entity.enums.BookingStatus.*;
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
    private final RoomRepository roomRepository;
    private final BalanceService balanceService;
    private final RoomService roomService;
    private final UserService userService;
    private final BookingMapper bookingMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public BookingResponse createBooking(String email, BookingRequest request) {
        log.info("▶️ Запрос на создание бронирования. Пользователь: {}, Slug типа номера: {}, Заезд: {}, Выезд: {}",
                email, request.getRoomTypeSlug(), request.getCheckIn(), request.getCheckOut());

        // ===== ЭТАП 1: ВАЛИДАЦИЯ (ДО финансовых операций) =====

        // 1.1 Валидация дат
        validateBookingDates(request);

        // 1.2 Проверка пользователя
        UserEntity user = getUser(email);

        // 1.3 Проверка комнаты
        RoomEntity room = findAvailableRoom(request.getRoomTypeSlug(), request.getCheckIn(), request.getCheckOut());
        log.info("✅ Найдена доступная комната для бронирования. Номер комнаты: {}, Slug тип: {}",
                room.getRoomNumber(), room.getRoomType().getSlug());

        // 1.4 Расчет стоимости
        long nights =
                ChronoUnit.DAYS.between(
                        request.getCheckIn(),
                        request.getCheckOut()
                );
        BigDecimal totalPrice = roomService.calculatePriceForDays(room, nights);
        log.info("💰 Рассчитана стоимость бронирования. Ночей: {}, Сумма: {}", nights, totalPrice);

        // ===== ЭТАП 2: СОЗДАНИЕ БРОНИРОВАНИЯ =====
        BookingEntity booking = buildBooking(user, room, request, totalPrice);

        BookingEntity savedBooking = bookingRepository.save(booking);
        log.info("✅ Бронирование успешно создано. ID: {}, Комната: {}, Пользователь: {}, Стоимость: {}, Ночей: {}",
                savedBooking.getId(), room.getRoomNumber(), email, booking.getTotalPrice(), booking.getNights());

        return bookingMapper.toDto(savedBooking);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelBooking(String email, UUID id) {
        log.info("▶️ Запрос на отмену бронирования. ID: {}, Пользователь: {}", id, email);

        // Получение бронирования
        BookingEntity booking = getBooking(id);

        // Проверка прав владельца
        UserEntity user = getUser(email);

        validateBookingOwner(booking, user);

        // Проверка статуса бронирования
        validateBookingStatusForCancellation(booking);

        // Расчет суммы возврата
        long nights = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        BigDecimal refund = roomService.calculatePriceForDays(booking.getRoom(), (int) nights);
        log.info("💰 Рассчитана сумма возврата. Ночей: {}, Сумма: {}", nights, refund);

        // Выполнение возврата
        log.debug("💳 Возврат средств пользователю: {}", user.getId());

        /*String refundDescription = buildRefundDescription(booking.getRoom().getRoomNumber(),
                booking.getCheckInDate(), booking.getCheckOutDate());
        balanceService.deposit(user.getId(), refund, refundDescription);*/

        // Сохранение статуса отмены
        booking.setStatus(CANCELLED);
        bookingRepository.save(booking);
        log.info("✅ Бронирование успешно отменено. ID: {}, Возврат: {}", id, refund);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponse> getUserBookings(String email, Pageable pageable) {
        log.info("▶️ Запрос на получение бронирований пользователя. Пользователь: {}, Всего страниц: {}", email, pageable);

        // Проверка существования пользователя
        UserEntity user = getUser(email);

        Page<BookingEntity> bookings = bookingRepository.findByUser_IdOrderByCreatedAtDesc(user.getId(), pageable);
        log.info("✅ Бронирования получены. Количество: {}, Всего страниц: {}",
                bookings.getNumberOfElements(), bookings.getTotalPages());

        return bookings.map(bookingMapper::toDto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BookingResponse payBooking(String email, UUID id) {
        log.info("▶️ Запрос на оплату бронирования. ID: {}, Пользователь: {}", id, email);

        // Получение пользователя
        UserEntity user = getUser(email);

        // Получение бронирования
        BookingEntity booking = getBooking(id);

        // Проверка прав владельца
        validateBookingOwner(booking, user);

        // Проверка статуса бронирования
        validateBookingStatusForPay(booking);

        validateUserBalance(user, booking.getTotalPrice());

        balanceService.withdraw(user.getId(), booking.getTotalPrice(), buildPaymentDescription(
                booking.getRoom().getRoomNumber(),
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        ));

        confirmBooking(booking);

        BookingEntity savedBooking = bookingRepository.save(booking);
        log.info(
                "✅ Бронирование {} успешно оплачено пользователем {} на сумму {}",
                booking.getId(),
                user.getEmail(),
                booking.getTotalPrice()
        );

        return bookingMapper.toDto(savedBooking);
    }

    // ===== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ =====

    /**
     * Получает бронирование по ID.
     */
    private @NonNull BookingEntity getBooking(UUID id) {
        return bookingRepository.findById(id).orElseThrow(() -> {
            log.warn("⚠️ Бронирование не найдено. ID: {}", id);
            return new EntityNotFoundException("Бронирование не найдено.");
        });
    }

    /**
     * Проверяет владельца бронирования.
     * @param booking
     * @param user
     */
    private static void validateBookingOwner(BookingEntity booking, UserEntity user) {
        if (!booking.getUser().getId().equals(user.getId())) {
            log.warn("🚫 Попытка доступа к чужому бронированию. ID: {}, Владелец: {}, Заказчик: {}",
                    booking.getId(), booking.getUser().getEmail(), user.getEmail());
            throw new SecurityException("Вы не имеете доступа к этому бронированию.");
        }
    }

    /**
     * Проверяет доступность комнаты на указанный период.
     *
     * @param roomTypeSlug Slug типа комнаты
     * @param checkIn дата заезда
     * @param checkOut дата выезда
     * @throws IllegalStateException если нет доступных комнат
     */
    private RoomEntity findAvailableRoom(String roomTypeSlug, LocalDate checkIn, LocalDate checkOut) {

        List<RoomEntity> rooms = roomRepository
                .findByRoomType_SlugAndActiveTrue(roomTypeSlug);

        if (rooms.isEmpty()) {
            log.warn("⚠️ Тип номера не найден. Тип: {}", roomTypeSlug);
            throw new EntityNotFoundException("Тип номера не найден.");
        }

        RoomEntity availableRoom = null;

        for (RoomEntity room : rooms) {
            boolean booked =
                    bookingRepository
                            .existsByRoom_IdAndStatusNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                                    room.getId(),
                                    CANCELLED,
                                    checkOut,
                                    checkIn
                            );
            if (!booked) {
                availableRoom = room;
                break;
            }
        }

        if (availableRoom == null) {
            log.warn("⚠️ Нет доступных комнат для бронирования. Тип комнаты: {}, Заезд: {}, Выезд: {}",
                    roomTypeSlug, checkIn, checkOut);
            throw new IllegalStateException("Нет доступных комнат на указанный период.");
        }

        return availableRoom;
    }

    /**
     * Валидирует даты бронирования.
     *
     * @param request запрос бронирования.
     * @throws IllegalArgumentException если даты некорректны
     */
    private void validateBookingDates(BookingRequest request) {
        LocalDate checkIn = request.getCheckIn();
        LocalDate checkOut = request.getCheckOut();

        if (checkIn == null || checkOut == null) {
            log.warn("⚠️ Даты бронирования не установлены. Заезд: {}, Выезд: {}", checkIn, checkOut);
            throw new IllegalArgumentException("Даты заезда и выезда обязательны.");
        }

        if (!checkIn.isBefore(checkOut)) {
            log.warn("⚠️ Некорректные даты бронирования. Заезд: {}, Выезд: {}", checkIn, checkOut);
            throw new IllegalArgumentException("Дата заезда должна быть раньше даты выезда.");
        }

        if (checkIn.isBefore(LocalDate.now())) {
            log.warn("⚠️ Дата заезда в прошлом. Заезд: {}, Сегодня: {}", checkIn, LocalDate.now());
            throw new IllegalArgumentException("Дата заезда не может быть в прошлом."
            );
        }
    }

    private BookingEntity buildBooking(UserEntity user, RoomEntity room, BookingRequest request, BigDecimal totalPrice) {
        BookingEntity booking = new BookingEntity();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(request.getCheckIn());
        booking.setCheckOutDate(request.getCheckOut());
        booking.setGuestsCount(request.getGuestsCount());
        booking.setTotalPrice(totalPrice);
        booking.setStatus(PENDING);
        return booking;
    }

    private @NonNull UserEntity getUser(String email) {
        UserEntity user = userService.getUserByEmail(email);
        if (!user.isActive()) {
            log.warn("⚠️ Попытка бронирования/оплаты/отмены неактивным пользователем. Пользователь ID: {}", user.getId());
            throw new IllegalStateException("Ваш аккаунт неактивен.");
        }
        return user;
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
        if (booking.getStatus() == CANCELLED) {
            log.warn("⚠️ Бронирование уже отменено. ID: {}", booking.getId());
            throw new IllegalStateException("Бронирование уже отменено.");
        }

        if (booking.getStatus() == CHECKED_OUT) {
            log.warn("⚠️ Невозможно отменить завершенное бронирование. ID: {}", booking.getId());
            throw new IllegalStateException("Невозможно отменить завершенное бронирование.");
        }
    }

    /**
     * Проверяет возможность оплаты бронирования.
     *
     * @param booking бронирование для проверки
     * @throws IllegalStateException если оплата невозможна
     */
    private void validateBookingStatusForPay(BookingEntity booking) {
        if (booking.getStatus() != PENDING) {

            log.warn(
                    "⚠️ Попытка оплаты бронирования со статусом {}. ID: {}",
                    booking.getStatus(),
                    booking.getId()
            );

            throw new IllegalStateException(
                    "Оплатить можно только бронирование со статусом PENDING."
            );

        }
    }

    private static void confirmBooking(BookingEntity booking) {
        booking.setStatus(CONFIRMED);
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
    /*private String buildRefundDescription(String roomNumber, LocalDate checkIn, LocalDate checkOut) {
        return format("Возврат за отмену бронирования номера {0} с {1} по {2}", roomNumber, checkIn, checkOut);
    }*/
}