package com.kbc.kibi_coins.service;

import com.kbc.kibi_coins.model.Category;
import com.kbc.kibi_coins.model.CategoryEnum;
import com.kbc.kibi_coins.model.Expense;
import com.kbc.kibi_coins.repository.CategoryRepository;
import com.kbc.kibi_coins.repository.ExpenseRepository;
import com.kbc.kibi_coins.util.InvalidCategoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.kbc.kibi_coins.util.ConstantMessages.INVALID_CATEGORY;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public BigDecimal spentThisMonth(int year, short month) {
        return expenseRepository.findAllByDate_Month(year, month)
                .stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal spentThisYear(int year) {
        return expenseRepository.findAllByDate_Year(year)
                .stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal spentByCategory(String category) {
        if (ExpenseService.isCategoryInvalid(category)) {
            throw new InvalidCategoryException(String.format(INVALID_CATEGORY, category));
        }
        Category cat = categoryRepository.findByName(CategoryEnum.valueOf(category.toUpperCase()));

        return cat.getExpenses()
                .stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
