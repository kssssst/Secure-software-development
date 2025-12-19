package com.example.market.controller;

import com.example.market.model.User;
import com.example.market.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public String register(@RequestBody User newUser) {
        if (newUser.getPassword().length() < 6 || !newUser.getPassword().matches(".*[!@#$%^&*].*")) {
            return "Пароль слишком слабый! Минимум 6 символов и спецсимвол.";
        }

        if (userRepository.existsByEmail(newUser.getEmail())) {
            return "Пользователь с таким email уже существует!";
        }

        newUser.setRole("USER");
        userRepository.save(newUser);

        return "Пользователь успешно зарегистрирован!";
    }

    // Эндпоинт для получения CSRF токена (опционально)
    @GetMapping("/csrf-token")
    public String getCsrfToken(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        return csrfToken != null ? csrfToken.getToken() : "CSRF token not available";
    }
}