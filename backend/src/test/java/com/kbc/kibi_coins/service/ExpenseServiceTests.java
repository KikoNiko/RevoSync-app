package com.kbc.kibi_coins.service;
import com.kbc.kibi_coins.model.Category;
import com.kbc.kibi_coins.model.CategoryEnum;
import com.kbc.kibi_coins.model.Expense;
import com.kbc.kibi_coins.model.dto.ExpenseRequest;
import com.kbc.kibi_coins.model.dto.ExpenseResponse;
import com.kbc.kibi_coins.repository.CategoryRepository;
import com.kbc.kibi_coins.repository.ExpenseRepository;
import com.kbc.kibi_coins.util.ConstantMessages;
import com.kbc.kibi_coins.util.InvalidCategoryException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTests {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private CategoryService categoryService;
    @InjectMocks
    private ExpenseService expenseService;

    private static final ExpenseRequest expenseRequest = ExpenseRequest.builder()
            .amount(BigDecimal.valueOf(100.0))
            .category("FOOD")
            .date(LocalDate.now())
            .comment("Dinner with friends")
            .build();

    private static final Expense mockExpense = Expense.builder()
            .id(1L)
            .amount(BigDecimal.valueOf(100.0))
            .date(LocalDate.now())
            .comment("Groceries")
            .category(new Category(CategoryEnum.FOOD))
            .build();

    @Test
    void addExpense_shouldSaveExpense_whenCategoryIsValid() {
        Category mockCategory = new Category();
        mockCategory.setName(CategoryEnum.FOOD);

        when(categoryRepository.findByName(CategoryEnum.FOOD)).thenReturn(mockCategory);

        ExpenseResponse response = expenseService.addExpense(expenseRequest);

        assertNotNull(response);
        assertEquals(expenseRequest.getAmount(), response.getAmount());
        assertEquals(expenseRequest.getCategory(), response.getCategory());

        verify(categoryRepository, times(1)).findByName(CategoryEnum.FOOD);
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void addExpense_shouldThrowInvalidCategoryException_whenCategoryIsInvalid() {
        String invalidCategory = "INVALID";
        ExpenseRequest expenseRequest1 = ExpenseRequest.builder()
                .amount(BigDecimal.valueOf(50.0))
                .category(invalidCategory)
                .date(LocalDate.now())
                .comment("Test Invalid")
                .build();

        when(categoryService.isCategoryInvalid(invalidCategory)).thenReturn(true);

        InvalidCategoryException exception = assertThrows(
                InvalidCategoryException.class,
                () -> expenseService.addExpense(expenseRequest1)
        );

        assertEquals(String.format(ConstantMessages.INVALID_CATEGORY, invalidCategory), exception.getMessage());
        verifyNoInteractions(expenseRepository);
    }

    @Test
    void getExpenseById_shouldReturnExpenseResponse_whenExpenseExists() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(mockExpense));

        ExpenseResponse response = expenseService.getExpenseById(1L);

        assertNotNull(response);
        assertEquals(mockExpense.getId(), response.getId());
        assertEquals(mockExpense.getAmount(), response.getAmount());
        assertEquals(mockExpense.getDate(), response.getDate());

        verify(expenseRepository, times(1)).findById(1L);
    }

    @Test
    void getExpenseById_shouldThrowEntityNotFoundException_whenExpenseDoesNotExist() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> expenseService.getExpenseById(1L));
        verify(expenseRepository, times(1)).findById(1L);
    }

    @Test
    void getAllExpenses_shouldReturnSortedExpenseResponses_whenExpensesExist() {
        Expense olderExpense = Expense.builder()
                .id(2L)
                .amount(BigDecimal.valueOf(50.0))
                .date(LocalDate.now().minusDays(1))
                .comment("Transport")
                .category(new Category(CategoryEnum.TRANSPORTATION))
                .build();

        when(expenseRepository.findAll()).thenReturn(Arrays.asList(mockExpense, olderExpense));

        List<ExpenseResponse> responses = expenseService.getAllExpenses();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(mockExpense.getId(), responses.get(0).getId());
        assertEquals(olderExpense.getId(), responses.get(1).getId());

        verify(expenseRepository, times(1)).findAll();
    }

    @Test
    void getAllExpenses_shouldReturnEmptyList_whenNoExpensesExist() {
        when(expenseRepository.findAll()).thenReturn(List.of());

        List<ExpenseResponse> responses = expenseService.getAllExpenses();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(expenseRepository, times(1)).findAll();
    }

    @Test
    void deleteExpenseById_shouldDeleteExpense_whenExpenseExists() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(mockExpense));

        expenseService.deleteExpenseById(1L);

        verify(expenseRepository, times(1)).findById(1L);
        verify(expenseRepository, times(1)).delete(mockExpense);
    }

    @Test
    void deleteExpenseById_shouldThrowEntityNotFoundException_whenExpenseDoesNotExist() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> expenseService.deleteExpenseById(1L));

        verify(expenseRepository, times(1)).findById(1L);
        verify(expenseRepository, never()).delete(any());
    }


    @Test
    void getAllByCategory_shouldReturnExpenses_whenCategoryIsValid() {
        String category = "FOOD";
        when(categoryService.isCategoryInvalid(category)).thenReturn(false);

        Category category1 = new Category(CategoryEnum.FOOD);

        Expense expense1 = new Expense(1L, BigDecimal.valueOf(50), category1, LocalDate.of(2023, 1, 10), "Lunch");
        Expense expense2 = new Expense(2L, BigDecimal.valueOf(100), category1, LocalDate.of(2023, 1, 5), "Dinner");
        List<Expense> expenses = Arrays.asList(expense1, expense2);

        when(expenseRepository.getAllByCategory_NameOrderByDateDesc(CategoryEnum.FOOD)).thenReturn(expenses);

        ExpenseResponse response1 = new ExpenseResponse(1L, BigDecimal.valueOf(50), category, LocalDate.of(2023, 1, 10), "Lunch");
        ExpenseResponse response2 = new ExpenseResponse(2L, BigDecimal.valueOf(100), category, LocalDate.of(2023, 1, 5), "Dinner");

        List<ExpenseResponse> result = expenseService.getAllByCategory(category);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));

        verify(categoryService, times(1)).isCategoryInvalid(category);
        verify(expenseRepository, times(1)).getAllByCategory_NameOrderByDateDesc(CategoryEnum.FOOD);
    }


    @Test
    void getAllByCategory_shouldThrowException_whenCategoryIsInvalid() {
        String category = "INVALID";
        when(categoryService.isCategoryInvalid(category)).thenReturn(true);

        InvalidCategoryException exception = assertThrows(InvalidCategoryException.class, () -> expenseService.getAllByCategory(category));
        assertEquals(String.format("Category %s does not exist.", category), exception.getMessage());

        verify(categoryService, times(1)).isCategoryInvalid(category);
        verifyNoInteractions(expenseRepository);
    }

    @Test
    void getAllByCategory_shouldReturnEmptyList_whenNoExpensesFound() {
        String category = "TRANSPORTATION";
        when(categoryService.isCategoryInvalid(category)).thenReturn(false);
        when(expenseRepository.getAllByCategory_NameOrderByDateDesc(CategoryEnum.TRANSPORTATION)).thenReturn(Collections.emptyList());

        List<ExpenseResponse> result = expenseService.getAllByCategory(category);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(categoryService, times(1)).isCategoryInvalid(category);
        verify(expenseRepository, times(1)).getAllByCategory_NameOrderByDateDesc(CategoryEnum.TRANSPORTATION);
    }


}
