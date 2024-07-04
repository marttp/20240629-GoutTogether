package dev.tpcoder.goutbackend.tourcompany.model;

import jakarta.validation.constraints.NotBlank;

public record TourCompanyDto(
        Integer id,
        @NotBlank String name,
        String status) {
}
