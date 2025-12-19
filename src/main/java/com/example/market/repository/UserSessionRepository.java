package com.example.market.repository;

import com.example.market.model.UserSession;
import com.example.market.model.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    Optional<UserSession> findByRefreshToken(String refreshToken);

    List<UserSession> findByUserIdAndStatus(Long userId, SessionStatus status);

    List<UserSession> findByStatusAndExpiresAtBefore(SessionStatus status, LocalDateTime expiresAt);

    @Modifying
    @Query("UPDATE UserSession s SET s.status = :status WHERE s.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") SessionStatus status);

    @Modifying
    @Query("UPDATE UserSession s SET s.status = :newStatus WHERE s.user.id = :userId AND s.status = :oldStatus")
    void updateStatusForUser(@Param("userId") Long userId,
                             @Param("oldStatus") SessionStatus oldStatus,
                             @Param("newStatus") SessionStatus newStatus);
}