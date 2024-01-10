package com.pt.dto.res;

import com.pt.entity.Category;
import lombok.Data;

import java.util.List;

@Data
public class CategoryPage {
    private List<Category> categories;
    private Pagination pagination;

    public CategoryPage(CustomPage<List<Category>> productPage) {
        this.categories = productPage.getContent();
        this.pagination = productPage.getPagination();
    }
}
