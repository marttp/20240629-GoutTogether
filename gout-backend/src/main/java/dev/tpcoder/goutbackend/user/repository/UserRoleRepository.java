package dev.tpcoder.goutbackend.user.repository;

import org.springframework.data.repository.CrudRepository;
import dev.tpcoder.goutbackend.user.model.UserRole;

public interface UserRoleRepository extends CrudRepository<UserRole, Integer> {

}
