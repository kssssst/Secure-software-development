package com.example.market.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne private User sender;
    @ManyToOne private User receiver;
    @ManyToOne private Listing listing;
    private String content;
    private LocalDateTime sentAt;

    public Message() {}
    public Message(User sender, User receiver, Listing listing, String content) {
        this.sender = sender; this.receiver = receiver; this.listing = listing;
        this.content = content; this.sentAt = LocalDateTime.now();
    }

    // геттеры/сетеры...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }
    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }
    public Listing getListing() { return listing; }
    public void setListing(Listing listing) { this.listing = listing; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}
