package dev.tpcoder.goutbackend.auth;

import java.util.Optional;

public interface AuthService {

    Optional<UserLogin> findCredentialByUserId(int userId);

    Optional<UserLogin> findCredentialByUsername(String email);

    UserLogin createConsumerCredential(int userId, String email, String password);

    void deleteCredentialByUserId(int userId);
}
