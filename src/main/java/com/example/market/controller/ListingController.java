package com.example.market.controller;

import com.example.market.model.Listing;
import com.example.market.model.User;
import com.example.market.model.Category;
import com.example.market.repository.ListingRepository;
import com.example.market.repository.UserRepository;
import com.example.market.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class ListingController {
    @Autowired
    private ListingRepository repo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private CategoryRepository catRepo;

    @PostMapping
    public Listing create(@RequestBody Listing l) {
        // если в теле пришёл owner с id, то подхватим его
        if (l.getOwner() != null && l.getOwner().getId() != null) {
            User u = userRepo.findById(l.getOwner().getId()).orElse(null);
            l.setOwner(u);
        }
        if (l.getCategory() != null && l.getCategory().getId() != null) {
            Category c = catRepo.findById(l.getCategory().getId()).orElse(null);
            l.setCategory(c);
        }
        return repo.save(l);
    }

    @GetMapping
    public List<Listing> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public Listing get(@PathVariable Long id) { return repo.findById(id).orElse(null); }

    @PutMapping("/{id}")
    public Listing update(@PathVariable Long id, @RequestBody Listing l) {
        Listing exist = repo.findById(id).orElse(null);
        if (exist == null) return null;
        exist.setTitle(l.getTitle());
        exist.setDescription(l.getDescription());
        // простая логика для owner/category по id (если передали)
        if (l.getOwner() != null && l.getOwner().getId() != null) {
            User u = userRepo.findById(l.getOwner().getId()).orElse(null);
            exist.setOwner(u);
        }
        if (l.getCategory() != null && l.getCategory().getId() != null) {
            Category c = catRepo.findById(l.getCategory().getId()).orElse(null);
            exist.setCategory(c);
        }
        return repo.save(exist);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "ok";
    }
}
