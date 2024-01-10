package com.pt.controller;

import com.pt.dto.res.ProductPage;
import com.pt.dto.res.ProductResDTO;
import com.pt.dto.res.ResponseDTO;
import com.pt.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllProducts(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "desc") String order,
            @RequestParam(required = false, defaultValue = "createdAt") String sort_by,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false, defaultValue = "0") Double rating_filter,
            @RequestParam(required = false, defaultValue = "0") Long price_min,
            @RequestParam(required = false, defaultValue = "9999999999") Long price_max,
            @RequestParam(required = false, defaultValue = "") String name
    ) {
        // page and sort
        Sort sort = Objects.equals(order, "asc")
                ? Sort.by(sort_by).ascending()
                : Sort.by(sort_by).descending();
        Pageable paging = PageRequest.of(page - 1, limit, sort);
        ProductPage productsResPage = productService.getAll(
                paging, category, rating_filter, price_min, price_max, name);
        return ResponseEntity.ok(new ResponseDTO("Get products successfully", productsResPage));
    }

    @GetMapping("/{product_id}")
    public ResponseEntity<ResponseDTO> getProductById(@PathVariable int product_id) {
        ProductResDTO productResDTO = productService.getProductById(product_id);
        return ResponseEntity.ok(new ResponseDTO("Get product successfully", productResDTO));
    }
}
