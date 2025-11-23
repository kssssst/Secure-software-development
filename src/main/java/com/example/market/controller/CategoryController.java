package com.example.market.controller;

import com.example.market.model.Category;
import com.example.market.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryRepository repo;

    @PostMapping
    public Category create(@RequestBody Category c) {
        return repo.save(c);
    }

    @GetMapping
    public List<Category> all() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Category get(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Category update(@PathVariable Long id, @RequestBody Category c) {
        Category exist = repo.findById(id).orElse(null);
        if (exist == null) return null;
        exist.setName(c.getName());
        return repo.save(exist);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // доступ только для админов
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "ok";
    }
}
