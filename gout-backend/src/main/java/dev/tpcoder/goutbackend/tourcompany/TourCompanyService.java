package dev.tpcoder.goutbackend.tourcompany;

import dev.tpcoder.goutbackend.tourcompany.model.TourCompany;
import dev.tpcoder.goutbackend.tourcompany.model.RegisterTourCompanyDto;

public interface TourCompanyService {

    TourCompany registerTourCompany(RegisterTourCompanyDto payload);

    TourCompany approvedTourCompany(Integer id);

    void createLoginContext(RegisterTourCompanyDto body, TourCompany tourCompany);
}
