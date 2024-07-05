package dev.tpcoder.goutbackend.tour;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import dev.tpcoder.goutbackend.common.enumeration.TourCompanyStatus;
import dev.tpcoder.goutbackend.common.enumeration.TourStatus;
import dev.tpcoder.goutbackend.common.exception.EntityNotFound;
import dev.tpcoder.goutbackend.tour.dto.TourDto;
import dev.tpcoder.goutbackend.tour.model.Tour;
import dev.tpcoder.goutbackend.tour.model.TourCount;
import dev.tpcoder.goutbackend.tour.repository.TourCountRepository;
import dev.tpcoder.goutbackend.tour.repository.TourRepository;
import dev.tpcoder.goutbackend.tourcompany.model.TourCompany;
import dev.tpcoder.goutbackend.tourcompany.repository.TourCompanyRepository;

@ExtendWith(MockitoExtension.class)
class TourServiceTest {

    @InjectMocks
    private TourServiceImpl tourService;
    @Mock
    private TourRepository tourRepository;
    @Mock
    private TourCompanyRepository tourCompanyRepository;
    @Mock
    private TourCountRepository tourCountRepository;

    @Test
    void whenCreateTourThenReturnSuccess() {
        var activityDate = Instant.now().plus(Duration.ofDays(5));
        var payload = new TourDto(
                1,
                "Camping",
                "Campaign 3 days 2 night",
                "Forest",
                10,
                activityDate,
                TourStatus.PENDING.name());

        var mockTourCompany = new TourCompany(
                1,
                "Mart Tour",
                TourCompanyStatus.WAITING.name());
        when(tourCompanyRepository.findById(payload.tourCompanyId()))
                .thenReturn(Optional.of(mockTourCompany));

        var tour = new Tour(
                1,
                AggregateReference.to(mockTourCompany.id()),
                "Camping",
                "Campaign 3 days 2 night",
                "Forest",
                10,
                activityDate,
                TourStatus.PENDING.name());
        when(tourRepository.save(any(Tour.class)))
                .thenReturn(tour);

        var mockTourCount = new TourCount(1, AggregateReference.to(1), 0);
        when(tourCountRepository.save(any(TourCount.class)))
                .thenReturn(mockTourCount);

        var actual = tourService.createTour(payload);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(tour.id(), actual.id());
        Assertions.assertEquals(tour.tourCompanyId().getId(), actual.tourCompanyId().getId());
        Assertions.assertEquals(tour.title(), actual.title());
        Assertions.assertEquals(tour.description(), actual.description());
        Assertions.assertEquals(tour.location(), actual.location());
        Assertions.assertEquals(tour.numberOfPeople(), actual.numberOfPeople());
        Assertions.assertEquals(tour.activityDate(), actual.activityDate());
        Assertions.assertEquals(tour.status(), actual.status());
    }

    @Test
    void whenCreateTourButCompanyNotFoundThenReturnNotFound() {
        var payload = new TourDto(
                1,
                "Camping",
                "Campaign 3 days 2 night",
                "Forest",
                10,
                Instant.now().plus(Duration.ofDays(5)),
                TourStatus.PENDING.name());
        when(tourCompanyRepository.findById(anyInt()))
                .thenThrow(new EntityNotFound(String.format("Tour Company Id: %s not found", 1)));
        Assertions.assertThrows(EntityNotFound.class, () -> tourService.createTour(payload));
    }

    @Test
    void whenGetTourByIdThenReturnSuccess() {
        var tour = new Tour(
                1,
                AggregateReference.to(1),
                "Camping",
                "Campaign 3 days 2 night",
                "Forest",
                10,
                Instant.now().plus(Duration.ofDays(5)),
                TourStatus.PENDING.name());
        when(tourRepository.findById(anyInt()))
                .thenReturn(Optional.of(tour));

        var actual = tourService.getTourById(1);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(tour.id(), actual.id());
        Assertions.assertEquals(tour.tourCompanyId().getId(), actual.tourCompanyId().getId());
        Assertions.assertEquals(tour.title(), actual.title());
        Assertions.assertEquals(tour.description(), actual.description());
        Assertions.assertEquals(tour.location(), actual.location());
        Assertions.assertEquals(tour.numberOfPeople(), actual.numberOfPeople());
        Assertions.assertEquals(tour.activityDate(), actual.activityDate());
        Assertions.assertEquals(tour.status(), actual.status());
    }

    @Test
    void whenGetTourByIdThenReturnNotFound() {
        when(tourRepository.findById(anyInt()))
                .thenThrow(new EntityNotFound(String.format("Tour Company Id: %s not found", 1)));
        Assertions.assertThrows(EntityNotFound.class, () -> tourService.getTourById(1));
    }

    @Test
    void whenGetPageTourThenReturnSuccess() {
        List<Tour> tours = List.of();
        Page<Tour> pageTours = new PageImpl<>(tours);

        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(0, 5, sort);
        when(tourRepository.findAll(pageable))
                .thenReturn(pageTours);

        var actual = tourService.getPageTour(pageable);
        Assertions.assertTrue(actual.getContent().isEmpty());
    }
}
