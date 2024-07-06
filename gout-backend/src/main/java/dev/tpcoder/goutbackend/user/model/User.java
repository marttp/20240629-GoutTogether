package dev.tpcoder.goutbackend.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("user")
public record User(
        @Id
        Integer id,
        String firstName,
        String lastName,
        String phoneNumber) {

}
