package dev.tpcoder.goutbackend.auth.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.tpcoder.goutbackend.auth.dto.AuthenticatedUser;
import dev.tpcoder.goutbackend.auth.dto.LoginRequestDto;
import dev.tpcoder.goutbackend.auth.dto.LoginResponseDto;
import dev.tpcoder.goutbackend.auth.model.UserLogin;
import dev.tpcoder.goutbackend.auth.repository.UserLoginRepository;
import dev.tpcoder.goutbackend.common.exception.EntityNotFoundException;
import dev.tpcoder.goutbackend.user.model.User;

@Service
public class AuthServiceImpl implements AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserLoginRepository userLoginRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, TokenService tokenService, UserLoginRepository userLoginRepository) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
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
        var createdCredential = userLoginRepository.save(userCredential);
        logger.info("Created credential for user: {}", userId);
        return createdCredential;
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
                        () -> new EntityNotFoundException(
                                String.format("Credential for user Id: %d not found", userId)));
        userLoginRepository.delete(credential);
    }

    @Override
    public LoginResponseDto login(LoginRequestDto body) {
        var authInfo = new UsernamePasswordAuthenticationToken(body.username(), body.password());
        var authentication = authenticationManager.authenticate(authInfo);
        var authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        var token = tokenService.generateToken(authentication);
        return new LoginResponseDto(authenticatedUser.userId(), token);
    }
}
