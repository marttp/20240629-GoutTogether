package dev.tpcoder.goutbackend.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;

import dev.tpcoder.goutbackend.user.model.User;

public interface UserRepository extends ListCrudRepository<User, Integer> {

    Page<User> findByFirstNameContaining(String firstName, Pageable pageable);
}
