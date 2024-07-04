package dev.tpcoder.goutbackend.tourcompany;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import dev.tpcoder.goutbackend.common.exception.EntityNotFound;
import dev.tpcoder.goutbackend.tourcompany.model.TourCompany;
import dev.tpcoder.goutbackend.tourcompany.model.TourCompanyDto;
import dev.tpcoder.goutbackend.tourcompany.model.TourCompanyStatus;

@Service
public class TourCompanyServiceImpl implements TourCompanyService {

    private final Logger logger = LoggerFactory.getLogger(TourCompanyServiceImpl.class);
    private final TourCompanyRepository tourCompanyRepository;

    public TourCompanyServiceImpl(TourCompanyRepository tourCompanyRepository) {
        this.tourCompanyRepository = tourCompanyRepository;
    }

    @Override
    public TourCompany registerTourCompany(TourCompanyDto payload) {
        logger.debug("[registerTour] newly tour company is registering...");
        var companyName = payload.name();
        var tourCompany = new TourCompany(
            null, 
            companyName, 
            TourCompanyStatus.WAITING.name()
        );
        var newTourCompany = tourCompanyRepository.save(tourCompany);
        logger.debug("[registerTour] new tour company: {}", newTourCompany);
        return newTourCompany;
    }

    @Override
    public TourCompany approvedTourCompany(Integer id) {
        var tourCompany = tourCompanyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFound(String.format("Tour Company Id: %s not found", id)));
        tourCompany = new TourCompany(id, tourCompany.name(), TourCompanyStatus.APPROVED.name());
        return tourCompanyRepository.save(tourCompany);
    }

}
