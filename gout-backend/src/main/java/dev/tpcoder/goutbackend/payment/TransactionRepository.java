package dev.tpcoder.goutbackend.payment;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {

    Optional<Transaction> findOneByIdempotentKey(String idempotentKey);
}
