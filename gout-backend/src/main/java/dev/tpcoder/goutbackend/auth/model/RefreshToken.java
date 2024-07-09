package dev.tpcoder.goutbackend.auth.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("refresh_token")
public record RefreshToken(
        @Id Integer id,
        String token,
        Instant issuedDate,
        String usage,
        Integer resourceId,
        boolean isExpired) {

}