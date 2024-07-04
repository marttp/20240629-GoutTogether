package dev.tpcoder.goutbackend.tourcompany.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterTourCompanyDto(
        Integer id,
        @NotBlank String name,
        @NotBlank String username,
        @NotBlank String password,
        String status) {
}
