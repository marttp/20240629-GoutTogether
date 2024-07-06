package dev.tpcoder.goutbackend.wallet.repository;

import java.util.Optional;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.repository.CrudRepository;

import dev.tpcoder.goutbackend.user.model.User;
import dev.tpcoder.goutbackend.wallet.model.UserWallet;

public interface UserWalletRepository extends CrudRepository<UserWallet, Integer> {

    Optional<UserWallet> findOneByUserId(AggregateReference<User, Integer> userId);
}
