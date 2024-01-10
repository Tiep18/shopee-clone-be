package com.pt.repository;

import com.pt.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findByRatingGreaterThanEqualAndPriceBetweenAndNameContainingAndCategoryId(
            double rating, long priceMin, long priceMax, String name, int categoryId, Pageable pageable);

    Page<Product> findByRatingGreaterThanEqualAndPriceBetweenAndNameContaining(
            double rating, long priceMin, long priceMax, String name, Pageable pageable);
}
