package dev.tpcoder.goutbackend.auth;

import org.springframework.data.repository.CrudRepository;

public interface UserLoginRepository extends CrudRepository<UserLogin, Integer> {

}
