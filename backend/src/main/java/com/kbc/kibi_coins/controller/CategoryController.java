package com.kbc.kibi_coins.controller;

import com.kbc.kibi_coins.model.dto.CategoryDto;
import com.kbc.kibi_coins.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/api/categories")
    public List<CategoryDto> getAllCategories() {
        return categoryService.getAllCategories();
    }
}
