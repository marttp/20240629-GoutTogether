package dev.tpcoder.goutbackend.tour.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Table;

@Table("tour_count")
public record TourCount(
        @Id Integer id,
        AggregateReference<Tour, Integer> tourId,
        int amount) {

}
