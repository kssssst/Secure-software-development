package com.example.market.dto;

import com.example.market.model.Report;

public class ReportResponse {
    private Long id;
    private UserInfo reporter;
    private ListingInfo listing;
    private String reason;

    public ReportResponse() {}

    public ReportResponse(Report report) {
        this.id = report.getId();
        if (report.getReporter() != null) {
            this.reporter = new UserInfo(report.getReporter());
        }
        if (report.getListing() != null) {
            this.listing = new ListingInfo(report.getListing());
        }
        this.reason = report.getReason();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserInfo getReporter() { return reporter; }
    public void setReporter(UserInfo reporter) { this.reporter = reporter; }

    public ListingInfo getListing() { return listing; }
    public void setListing(ListingInfo listing) { this.listing = listing; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    // Внутренние классы для безопасного представления данных
    public static class UserInfo {
        private Long id;
        private String name;
        private String email;

        public UserInfo(com.example.market.model.User user) {
            this.id = user.getId();
            this.name = user.getName();
            this.email = user.getEmail();
            // Пароль и роль НЕ включаем!
        }

        // Геттеры
        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }

        // Сеттеры (опционально)
        public void setId(Long id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class ListingInfo {
        private Long id;
        private String title;
        private Double price;
        private String description;

        public ListingInfo(com.example.market.model.Listing listing) {
            this.id = listing.getId();
            this.title = listing.getTitle();
            this.price = listing.getPrice();
            this.description = listing.getDescription();
        }

        // Геттеры
        public Long getId() { return id; }
        public String getTitle() { return title; }
        public Double getPrice() { return price; }
        public String getDescription() { return description; }

        // Сеттеры (опционально)
        public void setId(Long id) { this.id = id; }
        public void setTitle(String title) { this.title = title; }
        public void setPrice(Double price) { this.price = price; }
        public void setDescription(String description) { this.description = description; }
    }
}