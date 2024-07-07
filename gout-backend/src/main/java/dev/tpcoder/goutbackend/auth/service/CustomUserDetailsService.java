package dev.tpcoder.goutbackend.auth.service;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dev.tpcoder.goutbackend.auth.dto.AuthenticatedUser;
import dev.tpcoder.goutbackend.auth.repository.UserLoginRepository;
import dev.tpcoder.goutbackend.common.enumeration.RoleEnum;
import dev.tpcoder.goutbackend.common.exception.EntityNotFoundException;
import dev.tpcoder.goutbackend.user.repository.UserRoleRepository;

@Primary
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserLoginRepository userLoginRepository;
    private final UserRoleRepository userRoleRepository;

    public CustomUserDetailsService(UserLoginRepository userLoginRepository, UserRoleRepository userRoleRepository) {
        this.userLoginRepository = userLoginRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userLogin = userLoginRepository.findOneByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Credential for %s not found", username)));
        var userId = userLogin.userId().getId();
        var userRole = userRoleRepository.findOneByUserId(AggregateReference.to(userId))
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("Role for username: %s not found", username)));
        var role = RoleEnum.CONSUMER;
        if (userRole.roleId().getId() == RoleEnum.ADMIN.getId()) {
            role = RoleEnum.ADMIN;
        }
        return new AuthenticatedUser(userId, userLogin.email(), userLogin.password(), role);
    }

}
