package dev.tpcoder.goutbackend.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import dev.tpcoder.goutbackend.user.model.Role;
import dev.tpcoder.goutbackend.user.repository.RoleRepository;

@Service
public class RoleService {

    private final Logger logger = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Iterable<Role> getAllRole() {
        var availableRoles = roleRepository.findAll();
        logger.info("availableRoles: {}", availableRoles);
        return availableRoles;
    }
}
