package com.example.market.repository;

import com.example.market.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByReporterIdAndListingId(Long reporterId, Long listingId);
}
