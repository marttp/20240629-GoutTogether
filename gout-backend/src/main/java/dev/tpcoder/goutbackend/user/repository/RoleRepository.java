package dev.tpcoder.goutbackend.user.repository;

import org.springframework.data.repository.CrudRepository;

import dev.tpcoder.goutbackend.user.model.Role;

public interface RoleRepository extends CrudRepository<Role, Integer>{

}
