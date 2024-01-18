package com.pt.dto.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pt.entity.Category;
import com.pt.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResDTO {
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
    private Category category;
    private String image;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> images;

    public ProductResDTO(Product product) {
        final String URL_PREFIX = "https://s3.ap-southeast-1.amazonaws.com/shopee-clone/product/";
        images = product.getImages().stream().map(image -> URL_PREFIX + image.getName())
                .collect(Collectors.toList());
        id = (product.getId());
        name = (product.getName());
        price = (product.getPrice());
        sold = (product.getSold());
        category = (product.getCategory());
        image = (product.getImage() != null && !product.getImage().isEmpty()
                ? URL_PREFIX + product.getImage() : null);
        description = (product.getDescription());
        createdAt = (product.getCreatedAt());
        updatedAt = (product.getUpdatedAt());
        view = (product.getView());
        quantity = (product.getQuantity());
        rating = (product.getRating());
        priceBeforeDiscount = (product.getPriceBeforeDiscount());
    }
}
