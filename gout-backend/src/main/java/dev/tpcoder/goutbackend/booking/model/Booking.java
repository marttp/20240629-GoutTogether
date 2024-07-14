package dev.tpcoder.goutbackend.booking.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Table;

import dev.tpcoder.goutbackend.tour.model.Tour;
import dev.tpcoder.goutbackend.user.model.User;

@Table("booking")
public record Booking(
        @Id Integer id,
        AggregateReference<User, Integer> userId,
        AggregateReference<Tour, Integer> tourId,
        String state,
        Instant bookingDate,
        Instant lastUpdated,
        String idempotentKey) {

}