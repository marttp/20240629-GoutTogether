package dev.tpcoder.goutbackend.auth.service;

import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.tpcoder.goutbackend.auth.dto.AuthenticatedUser;
import dev.tpcoder.goutbackend.auth.dto.LoginRequestDto;
import dev.tpcoder.goutbackend.auth.dto.LoginResponseDto;
import dev.tpcoder.goutbackend.auth.dto.LogoutDto;
import dev.tpcoder.goutbackend.auth.model.RefreshToken;
import dev.tpcoder.goutbackend.auth.model.UserLogin;
import dev.tpcoder.goutbackend.auth.repository.RefreshTokenRepository;
import dev.tpcoder.goutbackend.auth.repository.UserLoginRepository;
import static dev.tpcoder.goutbackend.common.Constants.TOKEN_TYPE;
import dev.tpcoder.goutbackend.common.exception.EntityNotFoundException;
import dev.tpcoder.goutbackend.user.model.User;

@Service
public class AuthServiceImpl implements AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserLoginRepository userLoginRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthServiceImpl(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
            RefreshTokenRepository refreshTokenRepository, TokenService tokenService,
            UserLoginRepository userLoginRepository) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
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
    @Transactional
    public LoginResponseDto login(LoginRequestDto body) {
        var authInfo = new UsernamePasswordAuthenticationToken(body.username(), body.password());
        var authentication = authenticationManager.authenticate(authInfo);
        var authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

        var now = Instant.now();
        var accessToken = tokenService.issueAccessToken(authentication, now);
        var refreshToken = tokenService.issueRefreshToken(authentication, now);

        logout(authentication);

        // Save new refresh token
        var prepareRefreshTokenModel = new RefreshToken(
                null,
                refreshToken,
                now,
                authenticatedUser.role().name(),
                authenticatedUser.userId(),
                false);
        refreshTokenRepository.save(prepareRefreshTokenModel);

        return new LoginResponseDto(
                authenticatedUser.userId(),
                TOKEN_TYPE,
                accessToken,
                refreshToken);
    }

    @Override
    public void logout(Authentication authentication) {
        var authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        refreshTokenRepository.updateRefreshTokenByResource(
                authenticatedUser.role().name(),
                authenticatedUser.userId(),
                true);
    }

    @Override
    public void logout(LogoutDto logoutDto) {
        refreshTokenRepository.updateRefreshTokenByResource(
                logoutDto.roles(),
                Integer.parseInt(logoutDto.sub()),
                true);
    }
}
