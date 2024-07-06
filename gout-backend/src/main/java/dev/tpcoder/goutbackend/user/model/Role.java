package dev.tpcoder.goutbackend.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("role")
public record Role(@Id Integer id, String name) {

}
