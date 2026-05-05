package com.skypeak.hotel.service.impl;

import com.skypeak.hotel.entity.BookingEntity;
import com.skypeak.hotel.entity.enums.BookingStatus;
import com.skypeak.hotel.repository.BookingRepository;
import com.skypeak.hotel.repository.RoomRepository;
import com.skypeak.hotel.repository.UserRepository;
import com.skypeak.hotel.service.BalanceService;
import com.skypeak.hotel.service.BookingService;
import com.skypeak.hotel.service.RoomService;
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

/**
 * Реализация сервиса для управления бронированиями.
 * <p>
 * Обеспечивает создание, отмену и просмотр бронирований. Все операции выполняются
 * в контексте транзакции для обеспечения консистентности данных.
 * </p>
 *
 * @author Дмитрий Ельцов
 * @see BookingService
 */
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final BalanceService balanceService;
    private final RoomService roomService;

    /**
     * {@inheritDoc}
     */
    @Override
    public BookingEntity createBooking(UUID userId, UUID roomId, LocalDate checkIn, LocalDate checkOut) {
        log.info("▶️ Запрос на создание бронирования. User: {}, Room: {}, Check-in: {}, Check-out: {}",
                userId, roomId, checkIn, checkOut);

        if (checkIn == null || checkOut == null || !checkIn.isBefore(checkOut)) {
            log.warn("⚠️ Некорректные даты бронирования. Check-in: {}, Check-out: {}", checkIn, checkOut);
            throw new IllegalArgumentException("Дата заезда должна быть раньше даты выезда.");
        }

        var user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("⚠️ Пользователь не найден. User ID: {}", userId);
            return new EntityNotFoundException("Пользователь не найден.");
        });

        var room = roomRepository.findById(roomId).orElseThrow(() -> {
            log.warn("⚠️ Комната не найдена. Room ID: {}", roomId);
            return new EntityNotFoundException("Комната не найдена.");
        });

        if (!room.isActive()) {
            log.warn("⚠️ Комната неактивна. Room: {}", roomId);
            throw new IllegalStateException("Комната неактивна.");
        }

        boolean bookingExists = bookingRepository.existsByRoom_IdAndStatusNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                roomId,
                BookingStatus.CANCELLED,
                checkOut,
                checkIn
        );

        if (bookingExists) {
            log.warn("⚠️ Комната уже забронирована на указанный период. Room: {}, Check-in: {}, Check-out: {}",
                    roomId, checkIn, checkOut);
            throw new IllegalStateException("Комната уже забронирована на указанный период.");
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal totalCost = roomService.calculatePriceForDays(room, (int) nights);
        log.info("💰 Рассчитана стоимость бронирования. Ночей: {}, Сумма: {}", nights, totalCost);

        balanceService.withdraw(
                userId,
                totalCost,
                "Оплата за бронирование номера " +
                        room.getRoomNumber() + " с " +
                        checkIn + " по " +
                        checkOut
        );

        BookingEntity booking = new BookingEntity();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setStatus(BookingStatus.CREATED);
        booking.setCreatedAt(LocalDateTime.now());

        BookingEntity savedBooking = bookingRepository.save(booking);
        log.info("✅ Бронирование успешно создано. Booking ID: {}, Room: {}, User: {}",
                savedBooking.getId(), room.getRoomNumber(), userId);
        return savedBooking;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelBooking(UUID bookingId, UUID userId) {
        log.info("▶️ Запрос на отмену бронирования. Booking ID: {}, User ID: {}", bookingId, userId);

        var booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.warn("⚠️ Бронирование не найдено. Booking ID: {}", bookingId);
            return new EntityNotFoundException("Бронирование не найдено.");
        });

        if (!booking.getUser().getId().equals(userId)) {
            log.warn("🚫 Попытка отмены чужого бронирования. Booking ID: {}, Owner: {}, Requester: {}",
                    bookingId, booking.getUser().getId(), userId);
            throw new SecurityException("Вы не имеете права отменять это бронирование");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            log.warn("⚠️ Бронирование уже отменено. Booking ID: {}", bookingId);
            throw new IllegalStateException("Бронирование уже отменено.");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            log.warn("⚠️ Невозможно отменить завершенное бронирование. Booking ID: {}", bookingId);
            throw new IllegalStateException("Невозможно отменить завершенное бронирование.");
        }

        long nights = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        BigDecimal refund = roomService.calculatePriceForDays(booking.getRoom(), (int) nights);
        log.info("💰 Рассчитана сумма возврата. Ночей: {}, Сумма возврата: {}", nights, refund);

        balanceService.deposit(
                userId,
                refund,
                "Возврат средств за отмененное бронирование номера " +
                        booking.getRoom().getRoomNumber() + " с " +
                        booking.getCheckInDate() + " по " +
                        booking.getCheckOutDate()
        );

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        log.info("✅ Бронирование успешно отменено. Booking ID: {}, Refund: {}", bookingId, refund);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<BookingEntity> getUserBookings(UUID userId, Pageable pageable) {
        log.info("▶️ Запрос на получение бронирований пользователя. User ID: {}, Pageable: {}", userId, pageable);

        if (!userRepository.existsById(userId)) {
            log.warn("⚠️ Пользователь не найден. User ID: {}", userId);
            throw new EntityNotFoundException("User not found");
        }

        Page<BookingEntity> bookings = bookingRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable);
        log.info("✅ Бронирования получены. Количество: {}, Всего страниц: {}",
                bookings.getNumberOfElements(), bookings.getTotalPages());
        return bookings;
    }
}
