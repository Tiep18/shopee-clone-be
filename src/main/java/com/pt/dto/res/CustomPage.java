package com.pt.dto.res;

import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class CustomPage<T> {
    private T content;
    private Pagination pagination = new Pagination();

    public CustomPage(T content, Page<?> page) {
        this.content = content;
        this.pagination.setPage(page.getPageable().getPageNumber() + 1);
        this.pagination.setLimit(page.getPageable().getPageSize());
        this.pagination.setPageSize(page.getTotalPages());
    }
}
