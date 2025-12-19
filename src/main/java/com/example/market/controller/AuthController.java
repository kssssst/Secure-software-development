package com.example.market.controller;

import com.example.market.model.User;
import com.example.market.repository.UserRepository;
import com.example.market.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    // Регистрация пользователя
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User newUser) {
        // Проверка пароля
        if (newUser.getPassword().length() < 6 || !newUser.getPassword().matches(".*[!@#$%^&*].*")) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Пароль слишком слабый! Минимум 6 символов и спецсимвол.")
            );
        }

        // Проверка email
        if (userRepository.existsByEmail(newUser.getEmail())) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Пользователь с таким email уже существует!")
            );
        }

        // Хеширование пароля
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setRole("USER");

        userRepository.save(newUser);

        return ResponseEntity.ok(Map.of("message", "Пользователь успешно зарегистрирован!"));
    }

    // Аутентификация (получение токенов)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
                                   HttpServletRequest httpRequest) {
        try {
            TokenService.TokenPair tokens = tokenService.authenticate(
                    request.getEmail(),
                    request.getPassword(),
                    httpRequest
            );

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", tokens.getAccessToken());
            response.put("refreshToken", tokens.getRefreshToken());
            response.put("accessTokenExpiry", tokens.getAccessTokenExpiry());
            response.put("refreshTokenExpiry", tokens.getRefreshTokenExpiry());
            response.put("tokenType", "Bearer");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Неверный email или пароль"));
        }
    }

    // Обновление токенов
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request,
                                     HttpServletRequest httpRequest) {
        try {
            TokenService.TokenPair tokens = tokenService.refreshTokens(
                    request.getRefreshToken(),
                    httpRequest
            );

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", tokens.getAccessToken());
            response.put("refreshToken", tokens.getRefreshToken());
            response.put("accessTokenExpiry", tokens.getAccessTokenExpiry());
            response.put("refreshTokenExpiry", tokens.getRefreshTokenExpiry());
            response.put("tokenType", "Bearer");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Недействительный refresh токен"));
        }
    }

    // Выход из системы
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshRequest request) {
        tokenService.logout(request.getRefreshToken());
        return ResponseEntity.ok(Map.of("message", "Успешный выход из системы"));
    }

    // Выход из всех устройств
    @PostMapping("/logout/all")
    public ResponseEntity<?> logoutAll(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        // Здесь нужно получить ID пользователя из токена и вызвать logoutAllSessions
        return ResponseEntity.ok(Map.of("message", "Выход из всех устройств выполнен"));
    }

    // DTO классы для запросов
    public static class LoginRequest {
        private String email;
        private String password;

        // Геттеры и сеттеры
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RefreshRequest {
        private String refreshToken;

        // Геттеры и сеттеры
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }
}