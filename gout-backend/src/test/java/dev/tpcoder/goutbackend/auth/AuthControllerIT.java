package dev.tpcoder.goutbackend.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import dev.tpcoder.goutbackend.auth.dto.LoginRequestDto;
import dev.tpcoder.goutbackend.auth.dto.LoginResponseDto;
import dev.tpcoder.goutbackend.auth.dto.RefreshTokenDto;
import dev.tpcoder.goutbackend.common.enumeration.RoleEnum;
import dev.tpcoder.goutbackend.config.AbstractIntegrationTest;
import dev.tpcoder.goutbackend.user.dto.UserCreationDto;

class AuthControllerIT extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeEach
    void setupUser() {
        restClient = RestClient.builder()
                .baseUrl(String.format("http://localhost:%d", port))
                .build();
        var body = new UserCreationDto(
                "Test",
                "Test",
                "0800000001",
                "test@test.com",
                "123456789");
        restClient.post()
                .uri("/api/v1/users")
                .body(body)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    @Test
    void shouldLoginSuccessful() {
        var body = new LoginRequestDto("test@test.com", "123456789");
        var actual = login(body);
        assertNotNull(actual.userId());
        assertFalse(actual.accessToken().isBlank());
        assertFalse(actual.refreshToken().isBlank());
        assertEquals("bearer", actual.tokenType());
    }

    @Test
    void shouldRefreshNewToken() {
        var loginRequestBody = new LoginRequestDto("test@test.com", "123456789");
        var loginResponse = login(loginRequestBody);
        var refreshTokenRequestBody = new RefreshTokenDto(RoleEnum.CONSUMER.name(), loginResponse.userId(),
                loginResponse.refreshToken());
        var actual = refreshToken(refreshTokenRequestBody);
        assertNotNull(actual.userId());
        assertFalse(actual.accessToken().isBlank());
        assertFalse(actual.refreshToken().isBlank());
        assertEquals("bearer", actual.tokenType());
    }

    @Test
    void shouldLogoutSuccessful() {
        var loginRequestBody = new LoginRequestDto("test@test.com", "123456789");
        var loginResponse = login(loginRequestBody);
        assertDoesNotThrow(() -> logout(loginResponse.accessToken()));
    }

    private LoginResponseDto login(LoginRequestDto body) {
        var entity = restClient.post()
                .uri("/api/v1/auth/login")
                .body(body)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(LoginResponseDto.class);
        return entity.getBody();
    }

    private LoginResponseDto refreshToken(RefreshTokenDto body) {
        var entity = restClient.post()
                .uri("/api/v1/auth/refresh")
                .body(body)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(LoginResponseDto.class);
        return entity.getBody();
    }

    private void logout(String accessToken) {
        var authHeader = String.format("Bearer %s", accessToken);
        restClient.post()
                .uri("/api/v1/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
    }

}
