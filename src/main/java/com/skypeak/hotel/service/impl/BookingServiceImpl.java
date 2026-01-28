package com.skypeak.hotel.service.impl;

import com.skypeak.hotel.entity.BookingEntity;
import com.skypeak.hotel.entity.enums.BookingStatus;
import com.skypeak.hotel.repository.BookingRepository;
import com.skypeak.hotel.repository.RoomRepository;
import com.skypeak.hotel.repository.UserRepository;
import com.skypeak.hotel.service.BookingService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
@RequiredArgsConstructor
@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Override
    public BookingEntity createBooking(UUID userId, UUID roomId, LocalDate checkIn, LocalDate checkOut) {

        if (checkIn == null || checkOut == null || !checkIn.isBefore(checkOut))
            throw new IllegalArgumentException("Check-in date must be before check-out date");

        var user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User not found"));

        var room = roomRepository.findById(roomId).orElseThrow(() ->
                new EntityNotFoundException("Room not found"));

        if (!room.isActive()) throw new IllegalStateException("Room is not active");

        boolean bookingExists = bookingRepository.existsByRoom_IdAndStatusNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                roomId,
                BookingStatus.CANCELLED,
                checkOut,
                checkIn
        );

        if (bookingExists) throw new IllegalStateException("Room is already booked for the selected dates");

        BookingEntity booking = new BookingEntity();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setStatus(BookingStatus.CREATED);
        booking.setCreatedAt(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    @Override
    public void cancelBooking(UUID bookingId, UUID userId) {

        var booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new EntityNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(userId)) throw new SecurityException("You are not allowed to cancel this booking");

        if (booking.getStatus() == BookingStatus.CANCELLED) throw new IllegalStateException("Booking is already cancelled");

        if (booking.getStatus() == BookingStatus.COMPLETED) throw new IllegalStateException("Completed booking cannot be cancelled");

        booking.setStatus(BookingStatus.CANCELLED);

        bookingRepository.save(booking);
    }

    @Override
    public Page<BookingEntity> getUserBookings(UUID userId, Pageable pageable) {

        if (!userRepository.existsById(userId)) throw new EntityNotFoundException("User not found");

        return bookingRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable);
    }
}
