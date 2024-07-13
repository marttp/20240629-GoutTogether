package dev.tpcoder.goutbackend.wallet.model;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Table;

import dev.tpcoder.goutbackend.tourcompany.model.TourCompany;
import dev.tpcoder.goutbackend.user.model.User;

@Table("transaction")
public record Transaction(
        @Id Integer id,
        AggregateReference<User, Integer> userId,
        AggregateReference<TourCompany, Integer> tourCompanyId,
        Instant transactionDate,
        BigDecimal amount,
        String type,
        String idempotentKey) {

}