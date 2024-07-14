package dev.tpcoder.goutbackend.booking.service;

import org.springframework.security.core.Authentication;

import dev.tpcoder.goutbackend.booking.dto.BookingInfoDto;
import dev.tpcoder.goutbackend.booking.dto.CancelBookingDto;
import dev.tpcoder.goutbackend.booking.dto.RequestBookingDto;

public interface BookingService {

    BookingInfoDto bookTour(Authentication authentication, RequestBookingDto body);

    BookingInfoDto cancelTour(Authentication authentication, CancelBookingDto body);
}
