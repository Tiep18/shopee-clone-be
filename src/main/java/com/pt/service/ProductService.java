package com.pt.service;

import com.pt.dto.res.ProductPage;
import com.pt.dto.res.ProductResDTO;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductPage getAll(Pageable paging, Integer category, Double ratingFilter, Long priceMin, Long priceMax, String name);

    ProductResDTO getProductById(int id);
}

