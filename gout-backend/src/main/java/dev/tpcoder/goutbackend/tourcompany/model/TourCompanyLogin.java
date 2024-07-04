package dev.tpcoder.goutbackend.tourcompany.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Table;

@Table("tour_company_login")
public record TourCompanyLogin(
        @Id Integer id,
        AggregateReference<TourCompany, Integer> tourCompanyId,
        String username,
        String password) {
}