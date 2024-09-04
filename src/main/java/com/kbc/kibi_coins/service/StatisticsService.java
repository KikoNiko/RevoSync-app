package com.kbc.kibi_coins.service;

import com.kbc.kibi_coins.model.Expense;
import com.kbc.kibi_coins.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final ExpenseRepository expenseRepository;

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
}
