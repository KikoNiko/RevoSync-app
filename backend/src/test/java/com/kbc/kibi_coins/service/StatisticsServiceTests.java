package com.kbc.kibi_coins.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import com.kbc.kibi_coins.model.CategoryEnum;
import com.kbc.kibi_coins.model.Expense;
import com.kbc.kibi_coins.model.dto.SpentByMonthDTO;
import com.kbc.kibi_coins.repository.CategoryRepository;
import com.kbc.kibi_coins.repository.ExpenseRepository;
import com.kbc.kibi_coins.util.InvalidCategoryException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTests {

    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private CategoryService categoryService;
    @InjectMocks
    private StatisticsService statisticsService;

    @Test
    void testSpentByMonth() {
        int year = 2024, month = 1;
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
        BigDecimal expectedAmount = new BigDecimal("100.00");

        when(expenseRepository.sumExpensesInDateRange(startDate, endDate)).thenReturn(expectedAmount);

        BigDecimal result = statisticsService.spentByMonth(year, month);

        assertEquals(expectedAmount, result);
        verify(expenseRepository).sumExpensesInDateRange(startDate, endDate);
    }

    @Test
    void testSpentByYear() {
        int year = 2024;
        BigDecimal expectedAmount = new BigDecimal("1200.00");

        when(expenseRepository.sumExpensesByYear(year)).thenReturn(expectedAmount);

        BigDecimal result = statisticsService.spentByYear(year);

        assertEquals(expectedAmount, result);
        verify(expenseRepository).sumExpensesByYear(year);
    }

    @Test
    void testSpentByCategory_ValidCategory() {
        String category = "FOOD";
        BigDecimal expectedAmount = new BigDecimal("300.00");

        when(categoryService.isCategoryInvalid(category)).thenReturn(false);
        when(expenseRepository.getAllByCategory_NameOrderByDateDesc(CategoryEnum.valueOf(category)))
                .thenReturn(List.of(new Expense(new BigDecimal("200.00")), new Expense(new BigDecimal("100.00"))));

        BigDecimal result = statisticsService.spentByCategory(category);

        assertEquals(expectedAmount, result);
        verify(expenseRepository).getAllByCategory_NameOrderByDateDesc(CategoryEnum.valueOf(category));
    }

    @Test
    void testSpentByCategory_InvalidCategory() {
        String category = "INVALID";

        when(categoryService.isCategoryInvalid(category)).thenReturn(true);

        assertThrows(InvalidCategoryException.class, () -> statisticsService.spentByCategory(category));
    }

    @Test
    void testGetAllSpentByAllMonths() {
        when(expenseRepository.sumExpensesByMonth(anyInt(), anyShort())).thenReturn(new BigDecimal("100.00"));

        List<SpentByMonthDTO> result = statisticsService.getAllSpentByAllMonths();

        assertEquals(12, result.size());
        assertEquals("January", result.get(0).getMonth());
        assertEquals(new BigDecimal("100.00"), result.get(0).getMoneySpent());
    }

}
