package com.example.market.repository;

import com.example.market.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    // Добавляем метод для поиска сессии по refresh-токену
    UserSession findByRefreshToken(String refreshToken);
}
