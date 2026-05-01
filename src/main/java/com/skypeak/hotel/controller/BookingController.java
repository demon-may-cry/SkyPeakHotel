package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.booking.BookingResponse;
import com.skypeak.hotel.dto.booking.CreateBookingRequest;
import com.skypeak.hotel.mapper.BookingMapper;
import com.skypeak.hotel.security.CustomUserDetails;
import com.skypeak.hotel.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Контроллер для управления бронированиями.
 * <p>
 * Предоставляет эндпоинты для создания, просмотра и отмены бронирований.
 * Требует аутентификацию пользователя для всех операций.
 *
 * @author Дмитрий Ельцов
 * @see BookingService
 * @see BookingMapper
 */
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    /**
     * Создает новое бронирование для пользователя.
     *
     * @param request     DTO с информацией о бронировании (ID комнаты, дата заезда, дата выезда).
     * @param userDetails данные аутентифицированного пользователя.
     * @return {@link BookingResponse} с данными созданного бронирования.
     * @throws IllegalArgumentException если даты невалидны или комната недоступна.
     * @throws jakarta.persistence.EntityNotFoundException если комната не найдена.
     */
    @PostMapping
    public BookingResponse createBooking(@RequestBody @Valid CreateBookingRequest request,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("▶️ Получен запрос на создание бронирования от пользователя {}. " +
                        "Комната: {}, Заезд: {}, Выезд: {}",
                userDetails.getId(), request.getRoomId(), request.getCheckIn(), request.getCheckOut());

        var booking = bookingService.createBooking(
                userDetails.getId(),
                request.getRoomId(),
                request.getCheckIn(),
                request.getCheckOut()
        );

        BookingResponse response = bookingMapper.toDto(booking);
        log.info("✅ Бронирование успешно создано. ID бронирования: {}, пользователь: {}",
                response.id(), userDetails.getId());
        return response;
    }

    /**
     * Возвращает пагинированный список всех бронирований пользователя.
     *
     * @param userDetails данные аутентифицированного пользователя.
     * @param pageable    параметры пагинации (page, size, sort).
     * @return {@link Page} с {@link BookingResponse} для пользователя.
     */
    @GetMapping("/my")
    public Page<BookingResponse> getMyBookings(@AuthenticationPrincipal CustomUserDetails userDetails,
                                               Pageable pageable) {
        log.info("▶️ Получен запрос на получение бронирований для пользователя {}. " +
                "Параметры пагинации: {}", userDetails.getId(), pageable);

        Page<BookingResponse> bookings = bookingService.getUserBookings(
                userDetails.getId(),
                pageable)
                .map(bookingMapper::toDto);

        log.info("✅ Успешно возвращен список из {} бронирований на странице {}",
                bookings.getNumberOfElements(), bookings.getNumber());
        return bookings;
    }

    /**
     * Отменяет бронирование по его ID.
     *
     * @param id          UUID бронирования для отмены.
     * @param userDetails данные аутентифицированного пользователя.
     * @throws jakarta.persistence.EntityNotFoundException если бронирование не найдено.
     * @throws IllegalArgumentException если пользователь не является владельцем бронирования или
     *                                  бронирование уже отменено.
     */
    @DeleteMapping("/{id}")
    public void cancelBooking(@PathVariable UUID id,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("▶️ Получен запрос на отмену бронирования {} от пользователя {}",
                id, userDetails.getId());

        bookingService.cancelBooking(id, userDetails.getId());

        log.info("✅ Бронирование {} успешно отменено пользователем {}",
                id, userDetails.getId());
    }

}

