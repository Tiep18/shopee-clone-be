package com.pt.service;

import com.pt.entity.Category;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    public List<Category> getAll(Pageable pageable);


    Category getById(int id);
}

