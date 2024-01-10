package com.pt.service.implement;

import com.pt.entity.Category;
import com.pt.repository.CategoryRepository;
import com.pt.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepo;


    @Override
    public List<Category> getAll(Pageable pageable) {
        Page<Category> categoryPages = categoryRepo.findAll(pageable);
        return categoryPages.getContent();
    }

    @Override
    public Category getById(int id) {
        return null;
    }
}
