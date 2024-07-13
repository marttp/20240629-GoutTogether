package dev.tpcoder.goutbackend.wallet.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import dev.tpcoder.goutbackend.wallet.model.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {

    Optional<Transaction> findOneByIdempotentKey(String idempotentKey);
}
