package dev.tpcoder.goutbackend.auth.service;

import java.util.Optional;

import dev.tpcoder.goutbackend.auth.dto.LoginRequestDto;
import dev.tpcoder.goutbackend.auth.dto.LoginResponseDto;
import dev.tpcoder.goutbackend.auth.model.UserLogin;

public interface AuthService {

    Optional<UserLogin> findCredentialByUserId(int userId);

    Optional<UserLogin> findCredentialByUsername(String email);

    UserLogin createConsumerCredential(int userId, String email, String password);

    void deleteCredentialByUserId(int userId);

    LoginResponseDto login(LoginRequestDto body);
}
