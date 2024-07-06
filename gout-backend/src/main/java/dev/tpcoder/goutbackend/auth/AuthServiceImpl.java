package dev.tpcoder.goutbackend.auth;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.tpcoder.goutbackend.common.exception.EntityNotFoundException;
import dev.tpcoder.goutbackend.user.model.User;

@Service
public class AuthServiceImpl implements AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserLoginRepository userLoginRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(PasswordEncoder passwordEncoder, UserLoginRepository userLoginRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userLoginRepository = userLoginRepository;
    }

    @Override
    public Optional<UserLogin> findCredentialByUsername(String email) {
        return userLoginRepository.findOneByEmail(email);
    }

    @Override
    public UserLogin createConsumerCredential(int userId, String email, String password) {
        AggregateReference<User, Integer> userReference = AggregateReference.to(userId);
        var encryptedPassword = passwordEncoder.encode(password);
        var userCredential = new UserLogin(null, userReference, email, encryptedPassword);
        logger.info("Created credential for user: {}", userId);
        return userCredential;
    }

    @Override
    public Optional<UserLogin> findCredentialByUserId(int userId) {
        AggregateReference<User, Integer> userReference = AggregateReference.to(userId);
        return userLoginRepository.findOneByUserId(userReference);
    }

    @Override
    public void deleteCredentialByUserId(int userId) {
        var credential = findCredentialByUserId(userId)
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("Credential for user Id: %d not found", userId)));
        userLoginRepository.delete(credential);
    }
}
