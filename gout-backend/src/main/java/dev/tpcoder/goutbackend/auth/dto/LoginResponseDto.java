package dev.tpcoder.goutbackend.auth.dto;

public record LoginResponseDto(
        Integer userId,
        String token) {

}
