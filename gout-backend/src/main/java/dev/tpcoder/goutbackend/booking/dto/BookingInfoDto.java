package dev.tpcoder.goutbackend.booking.dto;

public record BookingInfoDto(
        Integer bookingId,
        Integer userId,
        Integer tourId,
        String state,
        Integer qrReference) {

}
