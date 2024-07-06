package dev.tpcoder.goutbackend.wallet.model;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Table;

import dev.tpcoder.goutbackend.tourcompany.model.TourCompany;

@Table("tour_company_wallet")
public record TourCompanyWallet(
        @Id Integer id,
        AggregateReference<TourCompany, Integer> tourCompanyId,
        Instant lastUpdated,
        BigDecimal balance) {

}
