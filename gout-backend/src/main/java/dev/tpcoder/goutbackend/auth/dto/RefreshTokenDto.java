package dev.tpcoder.goutbackend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RefreshTokenDto(
    @NotBlank String usage,
    @NotNull Integer resourceId,
    @NotBlank String refreshToken) {

}
