package dev.tpcoder.goutbackend.user.repository;

import org.springframework.data.repository.CrudRepository;

import dev.tpcoder.goutbackend.user.model.User;

public interface UserRepository extends CrudRepository<User, Integer> {

}
