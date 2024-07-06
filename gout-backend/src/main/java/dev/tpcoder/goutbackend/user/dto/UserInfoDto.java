package dev.tpcoder.goutbackend.user.dto;

public record UserInfoDto(
        Integer id,
        String firstName,
        String lastName,
        String phoneNumber) {

}
