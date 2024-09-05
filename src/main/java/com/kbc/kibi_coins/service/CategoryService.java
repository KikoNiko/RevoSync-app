package com.kbc.kibi_coins.service;

import com.kbc.kibi_coins.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    //TODO: Fix this method
    public void addExpensesFromSubCategoriesToParents() {
        categoryRepository.findAll()
                .stream()
                .filter(cat -> cat.getParentCategory() != null)
                .forEach(cat -> {
                    cat.getParentCategory().setExpenses(cat.getExpenses());
                    categoryRepository.save(cat);
                });
    }
}
