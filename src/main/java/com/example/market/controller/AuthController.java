package com.example.market.controller;

import com.example.market.dto.LoginRequest;
import com.example.market.dto.RefreshRequest;
import com.example.market.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/refresh")
    public Map<String, String> refresh(@RequestBody RefreshRequest request) {
        return authService.refreshToken(request);
    }
}
