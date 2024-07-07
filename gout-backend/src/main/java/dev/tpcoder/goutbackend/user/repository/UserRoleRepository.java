package dev.tpcoder.goutbackend.user.repository;

import java.util.Optional;

import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.repository.CrudRepository;

import dev.tpcoder.goutbackend.user.model.User;
import dev.tpcoder.goutbackend.user.model.UserRole;

public interface UserRoleRepository extends CrudRepository<UserRole, Integer> {

    Optional<UserRole> findOneByUserId(AggregateReference<User, Integer> userId);
}
