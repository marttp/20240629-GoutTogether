package dev.tpcoder.goutbackend.wallet.repository;

import java.util.Optional;

import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.repository.CrudRepository;

import dev.tpcoder.goutbackend.tourcompany.model.TourCompany;
import dev.tpcoder.goutbackend.wallet.model.TourCompanyWallet;

public interface TourCompanyWalletRepository extends CrudRepository<TourCompanyWallet, Integer> {

    Optional<TourCompanyWallet> findOneByTourCompanyId(AggregateReference<TourCompany, Integer> tourCompanyId);
}
