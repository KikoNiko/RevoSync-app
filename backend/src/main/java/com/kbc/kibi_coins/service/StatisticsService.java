package com.kbc.kibi_coins.service;

import com.kbc.kibi_coins.model.CategoryEnum;
import com.kbc.kibi_coins.model.Expense;
import com.kbc.kibi_coins.repository.ExpenseRepository;
import com.kbc.kibi_coins.util.InvalidCategoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import static com.kbc.kibi_coins.util.ConstantMessages.INVALID_CATEGORY;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final ExpenseRepository expenseRepository;

    public BigDecimal spentByMonth(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
        return expenseRepository.sumExpensesInDateRange(startDate, endDate);
    }

    public BigDecimal spentThisYear(int year) {
        return expenseRepository.sumExpensesByYear(year);
    }

    public BigDecimal spentByCategory(String category) {
        if (ExpenseService.isCategoryInvalid(category)) {
            throw new InvalidCategoryException(String.format(INVALID_CATEGORY, category));
        }

        return expenseRepository
                .getAllByCategory_NameOrderByDateDesc(CategoryEnum.valueOf(category.toUpperCase()))
                .stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
