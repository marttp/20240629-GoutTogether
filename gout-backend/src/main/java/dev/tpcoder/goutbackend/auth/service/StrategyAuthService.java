package dev.tpcoder.goutbackend.auth.service;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dev.tpcoder.goutbackend.auth.UserLoginRepository;
import dev.tpcoder.goutbackend.auth.dto.AuthenticateUser;
import dev.tpcoder.goutbackend.common.enumeration.RoleEnum;
import dev.tpcoder.goutbackend.common.exception.EntityNotFoundException;
import dev.tpcoder.goutbackend.user.repository.UserRoleRepository;

@Primary
@Service
public class StrategyAuthService implements UserDetailsService {

    private final UserRoleRepository userRoleRepository;
    private final UserLoginRepository userLoginRepository;

    public StrategyAuthService(UserLoginRepository userLoginRepository, UserRoleRepository userRoleRepository) {
        this.userLoginRepository = userLoginRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var credential = userLoginRepository.findOneByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Username: %d not found", username)));

        var userId = credential.userId().getId();
        var userRole = userRoleRepository.findOneByUserId(AggregateReference.to(userId))
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("Role for username: %d not found", username)));
        var roleId =  userRole.roleId().getId();
        RoleEnum roleForUser = RoleEnum.CONSUMER;
        if (RoleEnum.ADMIN.getId() == roleId) {
            roleForUser = RoleEnum.ADMIN;
        }
        return new AuthenticateUser(userId, credential.email(), credential.password(), roleForUser);
    }

}
