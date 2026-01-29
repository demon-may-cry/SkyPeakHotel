package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.booking.BookingResponse;
import com.skypeak.hotel.dto.booking.CreateBookingRequest;
import com.skypeak.hotel.entity.BookingEntity;
import com.skypeak.hotel.repository.UserRepository;
import com.skypeak.hotel.service.BookingService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;

    @PostMapping
    public BookingResponse createBooking(@RequestBody @Valid CreateBookingRequest request,
                                         Authentication authentication) {

        String email = authentication.getName();

        var user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));

        var booking = bookingService.createBooking(
                user.getId(),
                request.getRoomId(),
                request.getCheckIn(),
                request.getCheckOut()
        );

        return toDto(booking);
    }

    @GetMapping("/my")
    public Page<BookingResponse> getMyBookings(Authentication authentication,
                                               Pageable pageable) {

        String email = authentication.getName();

        var user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));

        return bookingService.getUserBookings(user.getId(), pageable).map(this::toDto);
    }

    @DeleteMapping("/{id}")
    public void cancelBooking(@PathVariable UUID id,
                              Authentication authentication) {

        String email = authentication.getName();

        var user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));

        bookingService.cancelBooking(id, user.getId());
    }

    private BookingResponse toDto(BookingEntity booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getRoom().getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }

}

