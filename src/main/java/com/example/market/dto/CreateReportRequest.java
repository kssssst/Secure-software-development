package com.example.market.dto;

public class CreateReportRequest {
    private Long reporterId;
    private Long listingId;
    private String reason;

    // Конструкторы
    public CreateReportRequest() {}

    public CreateReportRequest(Long reporterId, Long listingId, String reason) {
        this.reporterId = reporterId;
        this.listingId = listingId;
        this.reason = reason;
    }

    // Геттеры и сеттеры
    public Long getReporterId() { return reporterId; }
    public void setReporterId(Long reporterId) { this.reporterId = reporterId; }

    public Long getListingId() { return listingId; }
    public void setListingId(Long listingId) { this.listingId = listingId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}