package dev.tpcoder.goutbackend.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.tpcoder.goutbackend.auth.dto.LoginRequestDto;
import dev.tpcoder.goutbackend.auth.dto.LoginResponseDto;
import dev.tpcoder.goutbackend.auth.service.AuthService;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody @Validated LoginRequestDto body) {
        return authService.login(body);
    }
    
}
