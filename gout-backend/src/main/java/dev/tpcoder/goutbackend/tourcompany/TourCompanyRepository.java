package dev.tpcoder.goutbackend.tourcompany;

import org.springframework.data.repository.CrudRepository;

import dev.tpcoder.goutbackend.tourcompany.model.TourCompany;

public interface TourCompanyRepository extends CrudRepository<TourCompany, Integer> {

}
