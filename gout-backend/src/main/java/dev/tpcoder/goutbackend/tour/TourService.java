package dev.tpcoder.goutbackend.tour;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import dev.tpcoder.goutbackend.tour.dto.TourDto;
import dev.tpcoder.goutbackend.tour.model.Tour;

public interface TourService {
    
    Tour createTour(TourDto body);

    Tour getTourById(int id);

    Page<Tour> getPageTour(Pageable pageable);
}
