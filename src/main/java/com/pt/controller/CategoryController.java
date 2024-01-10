package com.pt.controller;

import com.pt.dto.res.ResponseDTO;
import com.pt.entity.Category;
import com.pt.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
@CrossOrigin
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getAll(Pageable pageable) {
        List<Category> categories = categoryService.getAll(pageable);
        return ResponseEntity.ok(new ResponseDTO("Get categories successfully", categories));
    }
}
