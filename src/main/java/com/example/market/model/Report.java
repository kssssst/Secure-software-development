package com.example.market.model;

import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"reporter_id", "listing_id"})
})
public class Report {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reporter_id")
    private User reporter;

    @ManyToOne(optional = false)
    @JoinColumn(name = "listing_id")
    private Listing listing;

    private String reason;

    public Report() {}
    public Report(User reporter, Listing listing, String reason) {
        this.reporter = reporter; this.listing = listing; this.reason = reason;
    }

    // геттеры/сетеры...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getReporter() { return reporter; }
    public void setReporter(User reporter) { this.reporter = reporter; }
    public Listing getListing() { return listing; }
    public void setListing(Listing listing) { this.listing = listing; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
