package com.pt.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "products_images")
@Data
public class ImageProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

}
