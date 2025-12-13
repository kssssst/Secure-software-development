package com.example.market.repository;

import com.example.market.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {

    // Поиск по категории
    List<Listing> findByCategoryId(Long categoryId);

    // Объявления за период
    List<Listing> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Самый дешевый
    Listing findTopByCategoryIdOrderByPriceAsc(Long categoryId);

    // Самый дорогой
    Listing findTopByCategoryIdOrderByPriceDesc(Long categoryId);

    // Средняя цена
    @Query("SELECT AVG(l.price) FROM Listing l WHERE l.category.id = :categoryId")
    Double findAveragePriceByCategory(Long categoryId);

    // Поиск по названию
    List<Listing> findByTitleContainingIgnoreCase(String titlePart);
}
