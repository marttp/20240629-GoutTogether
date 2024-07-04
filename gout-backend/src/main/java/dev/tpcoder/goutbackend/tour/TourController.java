package dev.tpcoder.goutbackend.tour;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.tpcoder.goutbackend.tour.dto.TourDto;
import dev.tpcoder.goutbackend.tour.model.Tour;

@RestController
@RequestMapping("/api/v1/tours")
public class TourController {

    private final Logger logger = LoggerFactory.getLogger(TourController.class);

    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @GetMapping
    // Pagination in Spring Boot (Spring Data JDBC)
    public Page<Tour> getTours(
            @RequestParam(required = true) int page,
            @RequestParam(required = true) int size,
            @RequestParam(required = true) String sortField,
            @RequestParam(required = true) String sortDirection) {
        // 1-100 tours
        // Size - 20 [1-20][21-40][41-60][61-80][81-100] <- ASC
        // Page - 2
        // Sort - ASC, DESC
        Sort sort = Sort.by(Sort.Direction.valueOf(sortDirection.toUpperCase()), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        return tourService.getPageTour(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tour> getTourById(@PathVariable int id) {
        logger.info("Get tourId: {}", id);
        return ResponseEntity.ok(tourService.getTourById(id));
    }

    @PostMapping
    public ResponseEntity<Tour> createTour(@RequestBody @Validated TourDto body) {
        var newTour = tourService.createTour(body);
        var location = String.format("http://localhost/api/v1/tours/%d", newTour.id());
        return ResponseEntity.created(URI.create(location)).body(newTour);
    }
}
