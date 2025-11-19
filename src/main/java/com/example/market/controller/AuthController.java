package com.example.market.controller;

import com.example.market.model.User;
import com.example.market.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

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
}