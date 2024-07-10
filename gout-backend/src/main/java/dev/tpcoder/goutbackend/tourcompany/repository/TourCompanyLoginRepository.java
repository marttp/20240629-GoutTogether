package dev.tpcoder.goutbackend.tourcompany.repository;

import java.util.Optional;

import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.repository.CrudRepository;

import dev.tpcoder.goutbackend.tourcompany.model.TourCompany;
import dev.tpcoder.goutbackend.tourcompany.model.TourCompanyLogin;

public interface TourCompanyLoginRepository extends CrudRepository<TourCompanyLogin, Integer> {
    
    Optional<TourCompanyLogin> findOneByUsername(String username);

    Optional<TourCompanyLogin> findOneByTourCompanyId(AggregateReference<TourCompany, Integer> tourCompanyId);
}
