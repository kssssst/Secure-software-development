package com.example.market.controller;

import com.example.market.dto.CreateReportRequest;
import com.example.market.dto.ReportResponse;
import com.example.market.model.Report;
import com.example.market.model.User;
import com.example.market.model.Listing;
import com.example.market.repository.ReportRepository;
import com.example.market.repository.UserRepository;
import com.example.market.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired
    private ReportRepository repo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ListingRepository listingRepo;

    @PostMapping
    public Object create(@RequestBody CreateReportRequest request) {
        try {
            Long reporterId = request.getReporterId();
            Long listingId = request.getListingId();

            if (reporterId == null || listingId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "reporter id and listing id required");
            }

            // проверяем, не жаловался ли уже этот пользователь на это объявление
            if (repo.findByReporterIdAndListingId(reporterId, listingId).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "already reported");
            }

            User u = userRepo.findById(reporterId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "reporter not found"));
            Listing l = listingRepo.findById(listingId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "listing not found"));

            Report r = new Report();
            r.setReporter(u);
            r.setListing(l);
            r.setReason(request.getReason());

            Report savedReport = repo.save(r);
            return new ReportResponse(savedReport);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating report: " + e.getMessage());
        }
    }

    @GetMapping
    public List<ReportResponse> all() {
        try {
            return repo.findAll()
                    .stream()
                    .map(ReportResponse::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching reports");
        }
    }

    @GetMapping("/{id}")
    public ReportResponse get(@PathVariable Long id) {
        try {
            return repo.findById(id)
                    .map(ReportResponse::new)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching report");
        }
    }

    // UPDATE
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ReportResponse updateReport(@PathVariable Long id, @RequestBody Report updatedReport) {
        try {
            Report savedReport = repo.findById(id)
                    .map(report -> {
                        // Обновляем reason, если предоставлено
                        if (updatedReport.getReason() != null) {
                            report.setReason(updatedReport.getReason());
                        }

                        // Обновляем reporter, если предоставлен новый
                        if (updatedReport.getReporter() != null && updatedReport.getReporter().getId() != null) {
                            User newReporter = userRepo.findById(updatedReport.getReporter().getId())
                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reporter not found"));
                            report.setReporter(newReporter);
                        }

                        // Обновляем listing, если предоставлен новый
                        if (updatedReport.getListing() != null && updatedReport.getListing().getId() != null) {
                            Listing newListing = listingRepo.findById(updatedReport.getListing().getId())
                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));
                            report.setListing(newListing);
                        }

                        // Проверяем уникальность пары reporter-listing (если изменились)
                        if (report.getReporter() != null && report.getListing() != null) {
                            // Исключаем текущую запись из проверки
                            if (repo.findByReporterIdAndListingId(report.getReporter().getId(), report.getListing().getId())
                                    .filter(existing -> !existing.getId().equals(id))
                                    .isPresent()) {
                                throw new ResponseStatusException(HttpStatus.CONFLICT, "This reporter has already reported this listing");
                            }
                        }

                        return repo.save(report);
                    })
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));

            return new ReportResponse(savedReport);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating report: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        try {
            if (!repo.existsById(id)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found");
            }
            repo.deleteById(id);
            return "ok";
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting report");
        }
    }
}