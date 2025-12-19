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

    // Получение всех пользователей
    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return repo.findAll().stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Получение одного пользователя по ID
    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {
        return repo.findById(id)
                .map(UserResponseDTO::new)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    // Пользователи с наибольшим количеством объявлений
    @GetMapping("/top-sellers")
    public List<UserResponseDTO> getTopSellers() {
        return repo.findTopUsersByListings()
                .stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }

    // CREATE
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public UserResponseDTO createUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = repo.save(user);
        return new UserResponseDTO(savedUser);
    }


    // UPDATE
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public UserResponseDTO updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return repo.findById(id)
                .map(user -> {
                    user.setName(updatedUser.getName());
                    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                        user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                    }
                    user.setEmail(updatedUser.getEmail());
                    // Можно добавить другие поля
                    return new UserResponseDTO(repo.save(user));
                })
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    // DELETE
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        repo.deleteById(id);
    }

}
