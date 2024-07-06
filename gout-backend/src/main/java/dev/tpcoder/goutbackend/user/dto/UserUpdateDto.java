package dev.tpcoder.goutbackend.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateDto(
                @NotBlank String firstName,
                @NotBlank String lastName) {

}
