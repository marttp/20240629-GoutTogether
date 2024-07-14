package dev.tpcoder.goutbackend.booking.repository;

import java.util.Optional;

import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.repository.CrudRepository;

import dev.tpcoder.goutbackend.booking.model.Booking;
import dev.tpcoder.goutbackend.tour.model.Tour;
import dev.tpcoder.goutbackend.user.model.User;

public interface BookingRepository extends CrudRepository<Booking, Integer> {

    Optional<Booking> findOneByIdempotentKey(String idempotentKey);

    Optional<Booking> findOneByUserIdAndTourId(
            AggregateReference<User, Integer> userId,
            AggregateReference<Tour, Integer> tourId);
}
