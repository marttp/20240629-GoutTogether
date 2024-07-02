package dev.tpcoder.goutbackend.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
