package dev.tpcoder.goutbackend.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.tpcoder.goutbackend.auth.dto.LoginRequestDto;
import dev.tpcoder.goutbackend.auth.dto.LoginResponseDto;
import dev.tpcoder.goutbackend.auth.dto.LogoutDto;
import dev.tpcoder.goutbackend.auth.dto.RefreshTokenDto;
import dev.tpcoder.goutbackend.auth.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Validated LoginRequestDto body) {
        return ResponseEntity.ok(authService.login(body));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refreshToken(@RequestBody @Validated RefreshTokenDto body) {
        return ResponseEntity.ok(authService.issueNewAccessToken(body));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        var jwt = (Jwt) authentication.getPrincipal();
        var logoutDto = new LogoutDto(jwt.getClaimAsString("sub"), jwt.getClaimAsString("roles"));
        authService.logout(logoutDto);
        return ResponseEntity.noContent().build();
    }
    
}
