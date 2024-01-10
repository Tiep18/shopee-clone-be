package com.pt.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PurchaseAddReqDTO {

    @JsonProperty("product_id")
    private int productId;

    @JsonProperty("buy_count")
    private int buyCount;
}
