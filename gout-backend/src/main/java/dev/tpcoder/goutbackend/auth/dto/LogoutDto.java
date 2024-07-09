package dev.tpcoder.goutbackend.auth.dto;

public record LogoutDto(
        String sub,
        String roles) {

}
