package dev.tpcoder.goutbackend.tour.service;

public interface TourCountService {
    
    void incrementTourCount(int tourId);

    void decrementTourCount(int tourId);
}
