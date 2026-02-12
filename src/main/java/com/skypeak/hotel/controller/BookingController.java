package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.booking.BookingResponse;
import com.skypeak.hotel.dto.booking.CreateBookingRequest;
import com.skypeak.hotel.mapper.BookingMapper;
import com.skypeak.hotel.security.CustomUserDetails;
import com.skypeak.hotel.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final BookingMapper bookingMapper;

    @PostMapping
    public BookingResponse createBooking(@RequestBody @Valid CreateBookingRequest request,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        var booking = bookingService.createBooking(
                userDetails.getId(),
                request.getRoomId(),
                request.getCheckIn(),
                request.getCheckOut()
        );

        return bookingMapper.toDto(booking);
    }

    @GetMapping("/my")
    public Page<BookingResponse> getMyBookings(@AuthenticationPrincipal CustomUserDetails userDetails,
                                               Pageable pageable) {

        return bookingService.getUserBookings(
                userDetails.getId(),
                pageable)
                .map(bookingMapper::toDto);
    }

    @DeleteMapping("/{id}")
    public void cancelBooking(@PathVariable UUID id,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {

        bookingService.cancelBooking(id, userDetails.getId());
    }

}

