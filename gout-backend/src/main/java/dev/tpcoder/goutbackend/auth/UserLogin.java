package dev.tpcoder.goutbackend.auth;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Table;

import dev.tpcoder.goutbackend.user.model.User;

@Table("user_login")
public record UserLogin(
        @Id
        Integer id,
        AggregateReference<User, Integer> userId,
        String email,
        String password) {

}
