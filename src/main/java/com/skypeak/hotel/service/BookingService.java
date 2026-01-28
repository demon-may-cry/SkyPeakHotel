package com.skypeak.hotel.service;

import com.skypeak.hotel.entity.BookingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
public interface BookingService {

    BookingEntity createBooking(UUID userId, UUID roomId, LocalDate checkIn, LocalDate checkOut);

    void cancelBooking(UUID bookingId, UUID userId);

    Page<BookingEntity> getUserBookings(UUID userId, Pageable pageable);

}
