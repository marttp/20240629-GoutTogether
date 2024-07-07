package dev.tpcoder.goutbackend.auth.dto;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import dev.tpcoder.goutbackend.common.enumeration.RoleEnum;

public record AuthenticatedUser(
        Integer userId,
        String email,
        String password,
        RoleEnum role) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return switch(this.role) {
            case RoleEnum.ADMIN -> List.of(new SimpleGrantedAuthority(RoleEnum.ADMIN.name()));
            default -> List.of(new SimpleGrantedAuthority(RoleEnum.CONSUMER.name()));
        };
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

}
