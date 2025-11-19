package com.example.market.controller;

import com.example.market.model.Report;
import com.example.market.model.User;
import com.example.market.model.Listing;
import com.example.market.repository.ReportRepository;
import com.example.market.repository.UserRepository;
import com.example.market.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;


import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired
    private ReportRepository repo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ListingRepository listingRepo;

    @PostMapping
    public Object create(@RequestBody Report r) {
        // очень простая проверка: нужны id reporter и listing
        Long reporterId = r.getReporter() != null ? r.getReporter().getId() : null;
        Long listingId = r.getListing() != null ? r.getListing().getId() : null;
        if (reporterId == null || listingId == null) {
            return "reporter id and listing id required";
        }
        // проверяем, не жаловался ли уже этот пользователь на это объявление
        if (repo.findByReporterIdAndListingId(reporterId, listingId).isPresent()) {
            return "already reported";
        }
        User u = userRepo.findById(reporterId).orElse(null);
        Listing l = listingRepo.findById(listingId).orElse(null);
        r.setReporter(u);
        r.setListing(l);
        return repo.save(r);
    }

    @GetMapping
    public List<Report> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public Report get(@PathVariable Long id) { return repo.findById(id).orElse(null); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // доступ только для админов
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "ok";
    }
}
