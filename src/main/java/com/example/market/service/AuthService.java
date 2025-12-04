package com.example.market.service;

import com.example.market.model.User;
import com.example.market.model.UserSession;
import com.example.market.repository.UserRepository;
import com.example.market.repository.UserSessionRepository;
import com.example.market.security.JwtTokenProvider;
import com.example.market.dto.LoginRequest;
import com.example.market.dto.RefreshRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserSessionRepository sessionRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       UserSessionRepository sessionRepository,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Map<String, String> login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null || !user.getPassword().equals(loginRequest.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        UserSession session = new UserSession(user, refreshToken);
        sessionRepository.save(session);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    public Map<String, String> refreshToken(RefreshRequest request) {
        UserSession session = sessionRepository.findByRefreshToken(request.getRefreshToken());
        if (session == null || session.getStatus() != session.getStatus().ACTIVE) {
            throw new RuntimeException("Refresh token is invalid or expired");
        }

        User user = session.getUser();
        String newAccessToken = jwtTokenProvider.generateAccessToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

        session.setStatus(session.getStatus().EXPIRED);
        sessionRepository.save(session);

        UserSession newSession = new UserSession(user, newRefreshToken);
        sessionRepository.save(newSession);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);
        return tokens;
    }
}
