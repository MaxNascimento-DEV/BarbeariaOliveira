package com.barbeariaoliveira.controller;

import org.springframework.web.bind.annotation.*;

import com.barbeariaoliveira.dto.LoginRequest;
import com.barbeariaoliveira.dto.LoginResponse;
import com.barbeariaoliveira.security.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/public/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

}
