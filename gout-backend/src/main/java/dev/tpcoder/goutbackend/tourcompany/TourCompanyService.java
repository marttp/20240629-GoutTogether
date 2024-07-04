package dev.tpcoder.goutbackend.tourcompany;

import dev.tpcoder.goutbackend.tourcompany.model.TourCompany;
import dev.tpcoder.goutbackend.tourcompany.model.TourCompanyDto;

public interface TourCompanyService {

    TourCompany registerTourCompany(TourCompanyDto payload);

    TourCompany approvedTourCompany(Integer id);
}
