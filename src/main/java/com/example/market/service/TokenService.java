package com.example.market.service;

import com.example.market.model.User;
import com.example.market.model.UserSession;
import com.example.market.model.SessionStatus;
import com.example.market.repository.UserRepository;
import com.example.market.repository.UserSessionRepository;
import com.example.market.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserSessionRepository userSessionRepository;
    private final UserRepository userRepository;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;

    public TokenService(JwtTokenProvider jwtTokenProvider,
                        AuthenticationManager authenticationManager,
                        UserSessionRepository userSessionRepository,
                        UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.userSessionRepository = userSessionRepository;
        this.userRepository = userRepository;
    }

    // Аутентификация пользователя и выдача токенов
    @Transactional
    public TokenPair authenticate(String username, String password, HttpServletRequest request) {
        // Аутентификация пользователя
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Генерация токенов
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // Получение пользователя
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Создание сессии
        UserSession session = new UserSession(
                user,
                refreshToken,
                LocalDateTime.now().plus(Duration.ofMillis(refreshExpiration))
        );

        // Добавляем информацию о запросе
        session.setIpAddress(request.getRemoteAddr());
        session.setUserAgent(request.getHeader("User-Agent"));

        userSessionRepository.save(session);

        return new TokenPair(accessToken, refreshToken);
    }

    // Обновление пары токенов
    @Transactional
    public TokenPair refreshTokens(String refreshToken, HttpServletRequest request) {
        // Валидация refresh токена
        if (!jwtTokenProvider.validateToken(refreshToken) ||
                !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Поиск активной сессии с этим refresh токеном
        Optional<UserSession> sessionOpt = userSessionRepository.findByRefreshToken(refreshToken);
        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("Session not found");
        }

        UserSession session = sessionOpt.get();

        // Проверка активности сессии
        if (!session.isActive()) {
            throw new RuntimeException("Session is not active");
        }

        // Помечаем старую сессию как обновленную
        session.setStatus(SessionStatus.REFRESHED);
        userSessionRepository.save(session);

        // Получаем пользователя
        User user = session.getUser();

        // Создаем новую аутентификацию
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                user.getAuthorities()
        );

        // Генерируем новую пару токенов
        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // Создаем новую сессию
        UserSession newSession = new UserSession(
                user,
                newRefreshToken,
                LocalDateTime.now().plus(Duration.ofMillis(refreshExpiration))
        );

        newSession.setIpAddress(request.getRemoteAddr());
        newSession.setUserAgent(request.getHeader("User-Agent"));

        userSessionRepository.save(newSession);

        return new TokenPair(newAccessToken, newRefreshToken);
    }

    // Выход из системы (отзыв токенов)
    @Transactional
    public void logout(String refreshToken) {
        Optional<UserSession> sessionOpt = userSessionRepository.findByRefreshToken(refreshToken);
        if (sessionOpt.isPresent()) {
            UserSession session = sessionOpt.get();
            session.revoke();
            userSessionRepository.save(session);
        }
    }

    // Отзыв всех сессий пользователя
    @Transactional
    public void logoutAllSessions(Long userId) {
        userSessionRepository.updateStatusForUser(
                userId,
                SessionStatus.ACTIVE,
                SessionStatus.REVOKED
        );
    }

    // Получение активных сессий пользователя
    public List<UserSession> getActiveSessions(Long userId) {
        return userSessionRepository.findByUserIdAndStatus(userId, SessionStatus.ACTIVE);
    }

    // Очистка истекших сессий
    @Transactional
    public void cleanupExpiredSessions() {
        List<UserSession> expiredSessions = userSessionRepository
                .findByStatusAndExpiresAtBefore(
                        SessionStatus.ACTIVE,
                        LocalDateTime.now()
                );

        for (UserSession session : expiredSessions) {
            session.setStatus(SessionStatus.EXPIRED);
        }

        userSessionRepository.saveAll(expiredSessions);
    }

    // DTO для пары токенов
    public static class TokenPair {
        private final String accessToken;
        private final String refreshToken;
        private final Date accessTokenExpiry;
        private final Date refreshTokenExpiry;

        public TokenPair(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.accessTokenExpiry = new Date(System.currentTimeMillis() + 900000); // 15 минут
            this.refreshTokenExpiry = new Date(System.currentTimeMillis() + 604800000); // 7 дней
        }

        // Геттеры
        public String getAccessToken() { return accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public Date getAccessTokenExpiry() { return accessTokenExpiry; }
        public Date getRefreshTokenExpiry() { return refreshTokenExpiry; }
    }
}