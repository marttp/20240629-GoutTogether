package dev.tpcoder.goutbackend.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserCreationDto(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String phoneNumber,
        @NotBlank String email,
        @NotBlank String password) {

}
