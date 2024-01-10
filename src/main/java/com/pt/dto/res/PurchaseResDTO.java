package com.pt.dto.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pt.entity.Purchase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseResDTO {
    private int id;
    @JsonProperty("buy_count")
    private int buyCount;
    private long price;
    private int status;
    private int user;
    @JsonProperty("price_before_discount")
    private long priceBeforeDiscount;
    private ProductResDTO product;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PurchaseResDTO(Purchase purchase) {
        id = (purchase.getId());
        buyCount = (purchase.getBuyCount());
        price = (purchase.getPrice());
        priceBeforeDiscount = (purchase.getPriceBeforeDiscount());
        status = (purchase.getStatus());
        user = (purchase.getAccount().getId());
        product = (new ProductResDTO(purchase.getProduct()));
        createdAt = (purchase.getCreatedAt());
        updatedAt = (purchase.getUpdatedAt());
    }
}
