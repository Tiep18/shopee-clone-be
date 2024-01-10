package com.pt.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private long price;
    private double rating;
    @JsonProperty("price_before_discount")
    private int priceBeforeDiscount;
    private int quantity;
    private int sold;
    private int view;
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private String image;

    @OneToMany(mappedBy = "product")
    private List<ImageProduct> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
