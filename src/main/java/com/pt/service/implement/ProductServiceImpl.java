package com.pt.service.implement;

import com.pt.dto.res.CustomPage;
import com.pt.dto.res.ProductPage;
import com.pt.dto.res.ProductResDTO;
import com.pt.entity.Product;
import com.pt.exception.NotFoundException;
import com.pt.repository.ProductRepository;
import com.pt.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepo;

    @Override
    public ProductPage getAll(
            Pageable paging, Integer category, Double ratingFilter, Long priceMin, Long priceMax, String name) {
        Page<Product> productsPage;
        if (category == null) {
            productsPage = productRepo.findByRatingGreaterThanEqualAndPriceBetweenAndNameContaining(
                    ratingFilter, priceMin, priceMax, name, paging
            );
        } else {
            productsPage = productRepo.findByRatingGreaterThanEqualAndPriceBetweenAndNameContainingAndCategoryId(
                    ratingFilter, priceMin, priceMax, name, category, paging
            );
        }
        List<ProductResDTO> productResDTOs = productsPage.getContent().stream().map(
                ProductResDTO::new).toList();
        return new ProductPage(new CustomPage<>(productResDTOs, productsPage));
    }

    @Override
    public ProductResDTO getProductById(int id) {
        Optional<Product> product = productRepo.findById(id);
        if (product.isPresent()) {
            return new ProductResDTO(product.get());
        } else {
            throw new NotFoundException("Product not found with id " + id);
        }
    }
}
