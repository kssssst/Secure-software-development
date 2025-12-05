package com.example.market.controller;

import com.example.market.model.User;
import com.example.market.repository.UserRepository;
import com.example.market.dto.UserResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Создание пользователя
    @PostMapping
    public UserResponseDTO create(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole("USER");

        User saved = repo.save(user);
        return new UserResponseDTO(saved);
    }

    // Получение всех пользователей (без пароля)
    @GetMapping
    public List<UserResponseDTO> all() {
        return repo.findAll()
                .stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Получение одного пользователя (без пароля)
    @GetMapping("/{id}")
    public UserResponseDTO get(@PathVariable Long id) {
        User user = repo.findById(id).orElse(null);
        if (user == null) return null;
        return new UserResponseDTO(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("#id == principal.id or hasRole('ADMIN')")
    public UserResponseDTO updateUser(@PathVariable Long id, @RequestBody User u) {
        User exist = repo.findById(id).orElse(null);
        if (exist == null) return null;

        exist.setName(u.getName());
        exist.setEmail(u.getEmail());

        User updated = repo.save(exist);
        return new UserResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // доступ только для админов
    public String deleteUser(@PathVariable Long id) {
        repo.deleteById(id);
        return "ok";
    }
}
