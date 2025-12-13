package com.example.market.dto;

import com.example.market.model.Listing;
import java.time.LocalDateTime;

public class ListingDTO {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private UserDto owner;
    private String categoryName;
    private LocalDateTime createdAt;

    public ListingDTO() {}

    // Конструктор из Listing
    public ListingDTO(Listing listing) {
        this.id = listing.getId();
        this.title = listing.getTitle();
        this.description = listing.getDescription();
        this.price = listing.getPrice();
        if (listing.getOwner() != null)
            this.owner = new UserDto(listing.getOwner());
        if (listing.getCategory() != null)
            this.categoryName = listing.getCategory().getName();
        this.createdAt = listing.getCreatedAt();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public UserDto getOwner() { return owner; }
    public void setOwner(UserDto owner) { this.owner = owner; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
