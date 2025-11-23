package com.example.market.controller;

import com.example.market.model.User;
import com.example.market.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Создание пользователя
    @PostMapping
    public User create(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole("USER");

        User saved = repo.save(user);
        System.out.println("Saved user ID: " + saved.getId());
        return saved;
    }

    @GetMapping
    public List<User> all() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    @PreAuthorize("#id == principal.id or hasRole('ADMIN')")
    public User updateUser(@PathVariable Long id, @RequestBody User u) {
        User exist = repo.findById(id).orElse(null);
        if (exist == null) return null;
        exist.setName(u.getName());
        exist.setEmail(u.getEmail());
        return repo.save(exist);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // доступ только для админов
    public String deleteUser(@PathVariable Long id) {
        repo.deleteById(id);
        return "ok";
    }

}
