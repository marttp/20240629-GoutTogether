package dev.tpcoder.goutbackend.tourcompany;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.tpcoder.goutbackend.tourcompany.model.TourCompany;
import dev.tpcoder.goutbackend.tourcompany.model.RegisterTourCompanyDto;

@RestController
@RequestMapping("/api/v1/tour-companies")
public class TourCompanyController {

    private final Logger logger = LoggerFactory.getLogger(TourCompanyController.class);
    private final TourCompanyService tourCompanyService;

    public TourCompanyController(TourCompanyService tourCompanyService) {
        this.tourCompanyService = tourCompanyService;
    }

    @PostMapping
    public ResponseEntity<TourCompany> registerNewTourCompany(@RequestBody @Validated RegisterTourCompanyDto body) {
        var tourCompany = tourCompanyService.registerTourCompany(body);
        return ResponseEntity.ok(tourCompany);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<TourCompany> approvedCompany(@PathVariable Integer id) {
        var approvedTourCompany = tourCompanyService.approvedTourCompany(id);
        logger.info("[approvedCompany] company id: {} is approved", id);
        return ResponseEntity.ok(approvedTourCompany);
    }
}
