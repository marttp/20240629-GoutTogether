package dev.tpcoder.goutbackend.auth.service;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import dev.tpcoder.goutbackend.auth.dto.AuthenticatedUser;
import dev.tpcoder.goutbackend.auth.model.RefreshToken;
import dev.tpcoder.goutbackend.auth.model.UserLogin;
import dev.tpcoder.goutbackend.auth.repository.RefreshTokenRepository;
import dev.tpcoder.goutbackend.tourcompany.model.TourCompanyLogin;

@Service
public class TokenService {

    private static final String ISSUER = "gout-backend";
    private static final String ROLES_CLAIM = "roles";
    private static final int TIME_FOR_ROTATE_SECONDS = 120;

    private final JwtEncoder jwtEncoder;
    private final long accessTokenExpiredInSeconds;
    private final long refreshTokenExpiredInSeconds;
    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenService(
            JwtEncoder jwtEncoder,
            @Value(value = "${token.access-token-expired-in-seconds}") long accessTokenExpiredInSeconds,
            @Value(value = "${token.refresh-token-expired-in-seconds}") long refreshTokenExpiredInSeconds,
            CustomUserDetailsService customUserDetailsService, 
            RefreshTokenRepository refreshTokenRepository) {
        this.accessTokenExpiredInSeconds = accessTokenExpiredInSeconds;
        this.jwtEncoder = jwtEncoder;
        this.refreshTokenExpiredInSeconds = refreshTokenExpiredInSeconds;
        this.customUserDetailsService = customUserDetailsService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String issueAccessToken(Authentication auth, Instant issueDate) {
        return generateToken(auth, issueDate, accessTokenExpiredInSeconds);
    }

    public String issueAccessToken(UserLogin userLogin, Instant issueDate) {
        AuthenticatedUser userDetails = (AuthenticatedUser) customUserDetailsService
                .loadUserByUsername(userLogin.email());
        return generateToken(userDetails, issueDate, accessTokenExpiredInSeconds);
    }

    public String issueAccessToken(TourCompanyLogin tourCompanyLogin, Instant issueDate) {
        AuthenticatedUser userDetails = (AuthenticatedUser) customUserDetailsService
                .loadUserByUsername(tourCompanyLogin.username());
        return generateToken(userDetails, issueDate, accessTokenExpiredInSeconds);
    }

    public String issueRefreshToken() {
        return UUID.randomUUID().toString();
    }

    public String generateToken(AuthenticatedUser auth, Instant issueDate, long expiredInSeconds) {
        return generateToken(auth.userId(), auth.getAuthorities(), issueDate, expiredInSeconds);
    }

    public String generateToken(Authentication auth, Instant issueDate, long expiredInSeconds) {
        var authenticatedUser = (AuthenticatedUser) auth.getPrincipal();
        return generateToken(authenticatedUser.userId(), auth.getAuthorities(), issueDate, expiredInSeconds);
    }

    private String generateToken(
            Integer userId,
            Collection<? extends GrantedAuthority> authorities,
            Instant issueDate,
            long expiredInSeconds) {

        Instant expire = issueDate.plusSeconds(expiredInSeconds);
        String scope = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(issueDate)
                .subject(String.valueOf(userId))
                .claim(ROLES_CLAIM, scope)
                .expiresAt(expire)
                .build();

        return encodeClaimToJwt(claims);
    }

    public String encodeClaimToJwt(JwtClaimsSet claims) {
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public boolean isRefreshTokenExpired(RefreshToken refreshToken) {
        var issuedDate = refreshToken.issuedDate();
        var expireDate = issuedDate.plusSeconds(refreshTokenExpiredInSeconds);
        var now = Instant.now();
        return now.isAfter(expireDate);
    }

    public String rotateRefreshTokenIfNeed(RefreshToken refreshTokenEntity) {
        var issuedDate = refreshTokenEntity.issuedDate();
        var expireDate = issuedDate.plusSeconds(refreshTokenExpiredInSeconds);
        var thresholdToRotateDate = expireDate.minusSeconds(TIME_FOR_ROTATE_SECONDS);
        var now = Instant.now();
        if (now.isAfter(thresholdToRotateDate)) {
            return issueRefreshToken();
        }
        return refreshTokenEntity.token();
    }

    public void cleanupRefreshTokenThatNotExpired() {
        var now = Instant.now();
        // Assume life of refresh token = 1 day
        // Token issued on 202407132216
        // Token expired on 202407142216
        // Cron start at 202407142216
        // If we want to check expired token from issuedDate -> minus seconds
        var thresholdDate = now.minusSeconds(refreshTokenExpiredInSeconds);
        refreshTokenRepository.updateRefreshTokenThatExpired(true, thresholdDate);
    }
}
