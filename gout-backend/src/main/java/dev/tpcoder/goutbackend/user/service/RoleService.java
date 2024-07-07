package dev.tpcoder.goutbackend.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Service;

import dev.tpcoder.goutbackend.common.enumeration.RoleEnum;
import dev.tpcoder.goutbackend.user.model.Role;
import dev.tpcoder.goutbackend.user.model.User;
import dev.tpcoder.goutbackend.user.model.UserRole;
import dev.tpcoder.goutbackend.user.repository.RoleRepository;
import dev.tpcoder.goutbackend.user.repository.UserRoleRepository;

@Service
public class RoleService {

    private final Logger logger = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public RoleService(RoleRepository roleRepository, UserRoleRepository userRoleRepository) {
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    public Iterable<Role> getAllRole() {
        var availableRoles = roleRepository.findAll();
        logger.info("availableRoles: {}", availableRoles);
        return availableRoles;
    }

    public UserRole bindingNewUser(int id, RoleEnum role) {
        AggregateReference<User, Integer> userId = AggregateReference.to(id);
        AggregateReference<Role, Integer> roleId = AggregateReference.to(role.getId());
        var prepareRole = new UserRole(null, userId, roleId);
        return userRoleRepository.save(prepareRole);
    }
}
