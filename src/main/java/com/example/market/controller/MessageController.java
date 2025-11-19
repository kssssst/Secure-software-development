package com.example.market.controller;

import com.example.market.model.Message;
import com.example.market.model.User;
import com.example.market.model.Listing;
import com.example.market.repository.MessageRepository;
import com.example.market.repository.UserRepository;
import com.example.market.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    @Autowired
    private MessageRepository repo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ListingRepository listingRepo;

    @PostMapping
    public Message create(@RequestBody Message m) {
        if (m.getSender() != null && m.getSender().getId() != null) {
            User s = userRepo.findById(m.getSender().getId()).orElse(null);
            m.setSender(s);
        }
        if (m.getReceiver() != null && m.getReceiver().getId() != null) {
            User r = userRepo.findById(m.getReceiver().getId()).orElse(null);
            m.setReceiver(r);
        }
        if (m.getListing() != null && m.getListing().getId() != null) {
            Listing l = listingRepo.findById(m.getListing().getId()).orElse(null);
            m.setListing(l);
        }
        return repo.save(m);
    }

    @GetMapping
    public List<Message> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public Message get(@PathVariable Long id) { return repo.findById(id).orElse(null); }

    @PutMapping("/{id}")
    public Message update(@PathVariable Long id, @RequestBody Message m) {
        Message exist = repo.findById(id).orElse(null);
        if (exist == null) return null;
        exist.setContent(m.getContent());

        if (m.getSender() != null && m.getSender().getId() != null) {
            User s = userRepo.findById(m.getSender().getId()).orElse(null);
            exist.setSender(s);
        }
        if (m.getReceiver() != null && m.getReceiver().getId() != null) {
            User r = userRepo.findById(m.getReceiver().getId()).orElse(null);
            exist.setReceiver(r);
        }
        if (m.getListing() != null && m.getListing().getId() != null) {
            Listing l = listingRepo.findById(m.getListing().getId()).orElse(null);
            exist.setListing(l);
        }
        return repo.save(exist);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // доступ только для админов
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "ok";
    }
}
