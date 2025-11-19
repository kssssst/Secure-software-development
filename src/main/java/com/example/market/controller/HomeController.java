package com.example.market.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/home")
    public String home() {
        return "Привет! Страницы доступны по /api/users и другим /api/*";
    }
}
