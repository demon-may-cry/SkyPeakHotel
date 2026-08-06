package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.booking.BookingResponse;
import com.skypeak.hotel.dto.booking.BookingRequest;
import com.skypeak.hotel.security.CurrentUserService;
import com.skypeak.hotel.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 */
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final CurrentUserService currentUserService;

    /**
     * Создает новое бронирование для пользователя.
     *
     * @param request DTO с информацией о бронировании
     *                (тип номера, даты проживания и количество гостей).
     * @return {@link BookingResponse} с данными созданного бронирования.
     * @throws IllegalArgumentException если даты невалидны или комната недоступна.
     * @throws jakarta.persistence.EntityNotFoundException если комната не найдена.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@RequestBody @Valid BookingRequest request) {

        String email = currentUserService.getCurrentUserEmail();
        log.info("▶️ Получен запрос на создание бронирования от пользователя {}. " +
                        "Тип комнаты: {}, Заезд: {}, Выезд: {}",
                email, request.getRoomTypeSlug(), request.getCheckIn(), request.getCheckOut());

        BookingResponse response = bookingService.createBooking(email, request);
        log.info("✅ Бронирование успешно создано. ID бронирования: {}, пользователь: {}",
                response.id(), email);

        return response;
    }

    @PostMapping("/{id}/pay")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse payBooking(@PathVariable UUID id) {
        String email = currentUserService.getCurrentUserEmail();
        log.info("▶️ Получен запрос на оплату бронирования {} от пользователя {}", id, email);

        BookingResponse response = bookingService.payBooking(email, id);
        log.info("✅ Бронирование {} успешно оплачено пользователем {}", id, email);

        return response;
    }

    /**
     * Возвращает историю бронирований текущего пользователя.
     *
     * @param pageable    параметры пагинации (page, size, sort).
     * @return {@link Page} с {@link BookingResponse} для пользователя.
     */
    @GetMapping("/me")
    public Page<BookingResponse> getMyBookings(@PageableDefault(
            sort = "createdAt",
            direction = Sort.Direction.DESC
    ) Pageable pageable) {

        String email = currentUserService.getCurrentUserEmail();
        log.info("▶️ Получен запрос на получение бронирований для пользователя {}. " +
                "Параметры пагинации: {}", email, pageable);

        Page<BookingResponse> bookings = bookingService.getUserBookings(
                email,
                pageable);
        log.info("✅ Успешно возвращен список из {} бронирований на странице {}",
                bookings.getNumberOfElements(), bookings.getNumber());

        return bookings;
    }

    /**
     * Отменяет бронирование по его ID.
     *
     * @param id          UUID бронирования для отмены.
     * @throws jakarta.persistence.EntityNotFoundException если бронирование не найдено.
     * @throws IllegalArgumentException если пользователь не является владельцем бронирования или
     *                                  бронирование уже отменено.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable UUID id) {

        String email = currentUserService.getCurrentUserEmail();
        log.info("▶️ Получен запрос на отмену бронирования {} от пользователя {}",
                id, email);

        bookingService.cancelBooking(email, id);
        log.info("✅ Бронирование {} успешно отменено пользователем {}",
                id, email);

        return ResponseEntity.noContent().build();
    }

}

