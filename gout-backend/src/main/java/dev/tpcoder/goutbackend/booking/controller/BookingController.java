package dev.tpcoder.goutbackend.booking.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.tpcoder.goutbackend.booking.dto.BookingInfoDto;
import dev.tpcoder.goutbackend.booking.dto.CancelBookingDto;
import dev.tpcoder.goutbackend.booking.dto.RequestBookingDto;
import dev.tpcoder.goutbackend.booking.service.BookingService;

import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingInfoDto bookTour(
            @RequestHeader("idempotent-key") String idempotentKey,
            @RequestBody @Validated RequestBookingDto body,
            Authentication authentication) {
        RequestBookingDto updatedBody = new RequestBookingDto(idempotentKey, body.userId(), body.tourId());
        return bookingService.bookTour(authentication, updatedBody);
    }

    @PostMapping("/cancel")
    public BookingInfoDto cancelTour(
            @RequestHeader("idempotent-key") String idempotentKey,
            @RequestBody @Validated CancelBookingDto body,
            Authentication authentication) {
        CancelBookingDto updatedBody = new CancelBookingDto(idempotentKey, body.bookingId(), body.userId(), body.tourId());
        return bookingService.cancelTour(authentication, updatedBody);
    }

}
