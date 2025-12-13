package com.example.market.repository;

import com.example.market.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    User findByEmail(String email);

    // 4. Пользователи с наибольшим количеством объявлений
    @Query("SELECT u FROM User u LEFT JOIN u.listings l GROUP BY u ORDER BY COUNT(l) DESC")
    List<User> findTopUsersByListings();
}
