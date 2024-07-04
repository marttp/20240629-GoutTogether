package dev.tpcoder.goutbackend.tourcompany;

import dev.tpcoder.goutbackend.tourcompany.dto.RegisterTourCompanyDto;
import dev.tpcoder.goutbackend.tourcompany.model.TourCompany;

public interface TourCompanyService {

    TourCompany registerTourCompany(RegisterTourCompanyDto payload);

    TourCompany approvedTourCompany(Integer id);
}
