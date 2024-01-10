package com.pt.dto.res;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class ProductPage {
    private List<ProductResDTO> products;
    private Pagination pagination;

    public ProductPage(CustomPage<List<ProductResDTO>> productPage) {
        this.products = productPage.getContent();
        this.pagination = productPage.getPagination();
    }
}
