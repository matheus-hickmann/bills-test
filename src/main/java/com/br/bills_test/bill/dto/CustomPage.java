package com.br.bills_test.bill.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class CustomPage<T> {
    List<T> content;
    int page;
    long totalElements;
    int size;

    public CustomPage(Page<T> page) {
        this.content = page.getContent();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
    }
}
