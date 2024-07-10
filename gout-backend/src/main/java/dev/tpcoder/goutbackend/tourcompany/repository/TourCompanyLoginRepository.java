package dev.tpcoder.goutbackend.tourcompany.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import dev.tpcoder.goutbackend.tourcompany.model.TourCompanyLogin;

public interface TourCompanyLoginRepository extends CrudRepository<TourCompanyLogin, Integer> {
    
    Optional<TourCompanyLogin> findOneByUsername(String username);
}
