package com.example.market.controller;

import com.example.market.dto.ListingDTO;
import com.example.market.dto.UserDto;
import com.example.market.model.Listing;
import com.example.market.model.User;
import com.example.market.model.Category;
import com.example.market.repository.ListingRepository;
import com.example.market.repository.UserRepository;
import com.example.market.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/api/listings")
public class ListingController {

    @Autowired
    private ListingRepository repo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CategoryRepository catRepo;

    // DTO для обновления объявления
    public static class ListingUpdateDTO {
        public String title;
        public String description;
        public Double price;
        public Long ownerId;
        public Long categoryId;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ListingUpdateDTO dto) {
        Listing l = new Listing();
        l.setTitle(dto.title);
        l.setDescription(dto.description);
        l.setPrice(dto.price);

        if (dto.ownerId != null) {
            Optional<User> owner = userRepo.findById(dto.ownerId);
            if (owner.isEmpty()) return ResponseEntity.badRequest().body("Owner not found");
            l.setOwner(owner.get());
        } else {
            return ResponseEntity.badRequest().body("Owner ID required");
        }

        if (dto.categoryId != null) {
            Optional<Category> category = catRepo.findById(dto.categoryId);
            if (category.isEmpty()) return ResponseEntity.badRequest().body("Category not found");
            l.setCategory(category.get());
        } else {
            return ResponseEntity.badRequest().body("Category ID required");
        }

        l.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.ok(new ListingDTO(repo.save(l)));
    }

    @GetMapping
    public List<ListingDTO> all() {
        return repo.findAll().stream()
                .map(ListingDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Optional<Listing> listingOpt = repo.findById(id);
        if (listingOpt.isPresent()) {
            return ResponseEntity.ok(new ListingDTO(listingOpt.get()));
        } else {
            return ResponseEntity.badRequest().body("Listing not found");
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ListingUpdateDTO dto) {
        Optional<Listing> existOpt = repo.findById(id);
        if (existOpt.isEmpty()) return ResponseEntity.badRequest().body("Listing not found");

        Listing exist = existOpt.get();
        exist.setTitle(dto.title);
        exist.setDescription(dto.description);
        exist.setPrice(dto.price);

        if (dto.ownerId != null) {
            Optional<User> owner = userRepo.findById(dto.ownerId);
            if (owner.isEmpty()) return ResponseEntity.badRequest().body("Owner not found");
            exist.setOwner(owner.get());
        }

        if (dto.categoryId != null) {
            Optional<Category> category = catRepo.findById(dto.categoryId);
            if (category.isEmpty()) return ResponseEntity.badRequest().body("Category not found");
            exist.setCategory(category.get());
        }

        return ResponseEntity.ok(new ListingDTO(repo.save(exist)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.badRequest().body("Listing not found");
        repo.deleteById(id);
        return ResponseEntity.ok("Listing deleted");
    }


    // 1. Поиск объявлений по категории
    @GetMapping("/category/{id}")
    public List<ListingDTO> getByCategory(@PathVariable Long id) {
        return repo.findByCategoryId(id).stream()
                .map(ListingDTO::new)
                .collect(Collectors.toList());
    }

    // 2. Объявления за сегодня
    @GetMapping("/today")
    public List<ListingDTO> getListingsToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        return repo.findByCreatedAtBetween(start, end).stream()
                .map(ListingDTO::new)
                .collect(Collectors.toList());
    }

    // 3. Самый дешевый товар в категории
    @GetMapping("/category/{id}/cheapest")
    public ResponseEntity<?> getCheapestInCategory(@PathVariable Long id) {
        Listing l = repo.findTopByCategoryIdOrderByPriceAsc(id);
        return l != null ? ResponseEntity.ok(new ListingDTO(l)) : ResponseEntity.badRequest().body("No listings found");
    }

    // 4. Самый дорогой товар в категории
    @GetMapping("/category/{id}/most-expensive")
    public ResponseEntity<?> getMostExpensive(@PathVariable Long id) {
        Listing l = repo.findTopByCategoryIdOrderByPriceDesc(id);
        return l != null ? ResponseEntity.ok(new ListingDTO(l)) : ResponseEntity.badRequest().body("No listings found");
    }

    // 5. Средняя цена товаров в категории
    @GetMapping("/category/{id}/average-price")
    public Double getAveragePrice(@PathVariable Long id) {
        return repo.findAveragePriceByCategory(id);
    }

    // 6. Поиск по названию
    @GetMapping("/search")
    public List<ListingDTO> search(@RequestParam String q) {
        return repo.findByTitleContainingIgnoreCase(q).stream()
                .map(ListingDTO::new)
                .collect(Collectors.toList());
    }

    // 7. Пользователь с наибольшим количеством объявлений
    @GetMapping("/top-user")
    public ResponseEntity<?> getTopUser() {
        List<User> users = userRepo.findAll();

        Optional<User> top = users.stream()
                .max(Comparator.comparingInt(u -> u.getListings() != null ? u.getListings().size() : 0));

        if (top.isPresent()) {
            UserDto dto = new UserDto(top.get());
            return ResponseEntity.ok(dto); // UserDto без пароля
        } else {
            return ResponseEntity.badRequest().body("No users found");
        }
    }


}
