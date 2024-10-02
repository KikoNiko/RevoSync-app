package com.kbc.kibi_coins.service;

import com.kbc.kibi_coins.model.Category;
import com.kbc.kibi_coins.model.dto.CategoryDto;
import com.kbc.kibi_coins.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private CategoryDto map(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getCategoryId());
        categoryDto.setName(category.getName().name());
        return categoryDto;
    }
}
