package dev.tpcoder.goutbackend.tour.repository;

import org.springframework.data.repository.CrudRepository;

import dev.tpcoder.goutbackend.tour.model.TourCount;

public interface TourCountRepository extends CrudRepository<TourCount, Integer> {

}
