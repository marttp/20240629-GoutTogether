package dev.tpcoder.goutbackend.tour;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tours")
public class TourController {

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);
    private final Map<Integer, Tour> tourInMemDb;

    public TourController() {
        tourInMemDb = new HashMap<>();
    }
    // CRUD - Tour
    // C - Create Tour
    // R - Read, Get 1 tour
    // U - Update on specific record
    // D - Delete from InMemory DB

    // Get All
    @GetMapping
    public List<Tour> getTours() {
        return tourInMemDb.entrySet().stream()
                .map(e -> e.getValue())
                .toList();
    }

    // Get single
    @GetMapping("/{id}")
    public Tour getTourById(@PathVariable int id) {
        return Optional.ofNullable(tourInMemDb.get(id))
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tour createTour(@RequestBody Tour tour) {
        var newTour = new Tour(
            ATOMIC_INTEGER.getAndIncrement(), 
            tour.title(), 
            tour.maxPeople()
        );
        var id = newTour.id();
        tourInMemDb.put(id, newTour);
        return tourInMemDb.get(id);
    }

    @PutMapping("/{id}")
    public Tour putMethodName(@PathVariable int id, @RequestBody Tour tour) {
        var updatedTour = new Tour(
            id, 
            tour.title(), 
            tour.maxPeople()
        );
        tourInMemDb.put(id, updatedTour);
        return tourInMemDb.get(id);
    }

    @DeleteMapping("/{id}")
    public String deleteTour(@PathVariable int id) {
        if (!tourInMemDb.containsKey(id)) {
            return "Failed";
        }
        tourInMemDb.remove(id);
        return "Success to delete " + id;
    }
}
