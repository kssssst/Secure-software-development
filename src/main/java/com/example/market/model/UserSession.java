package com.example.market.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions")
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String refreshToken;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    public UserSession() {}

    public UserSession(User user, String refreshToken) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.createdAt = LocalDateTime.now();
        this.status = SessionStatus.ACTIVE;
    }

    // Getters и Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public SessionStatus getStatus() { return status; }
    public void setStatus(SessionStatus status) { this.status = status; }
}
