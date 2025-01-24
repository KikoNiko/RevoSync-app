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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExpenseServiceTests {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private CategoryService categoryService;
    @InjectMocks
    private ExpenseService expenseService;

    private ExpenseRequest expenseRequest;
    private Expense mockExpense;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        expenseRequest = ExpenseRequest.builder()
                .amount(BigDecimal.valueOf(100.0))
                .category("FOOD")
                .date(LocalDate.now())
                .comment("Dinner with friends")
                .build();

        mockExpense = Expense.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(100.0))
                .date(LocalDate.now())
                .comment("Groceries")
                .category(new Category(CategoryEnum.FOOD))
                .build();
    }

    @Test
    void addExpense_shouldSaveExpense_whenCategoryIsValid() {
        Category mockCategory = new Category();
        mockCategory.setName(CategoryEnum.FOOD);

        when(categoryRepository.findByName(CategoryEnum.FOOD)).thenReturn(mockCategory);

        ExpenseResponse response = expenseService.addExpense(expenseRequest);

        assertNotNull(response);
        assertEquals(expenseRequest.getAmount(), response.getAmount());
        assertEquals(expenseRequest.getCategory(), response.getCategory());
        assertEquals(expenseRequest.getComment(), response.getComment());
        assertEquals(expenseRequest.getDate(), response.getDate());

        verify(categoryRepository, times(1)).findByName(CategoryEnum.FOOD);
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void addExpense_shouldThrowInvalidCategoryException_whenCategoryIsInvalid() {
        String invalidCategory = "INVALID_CATEGORY";
        expenseRequest.setCategory(invalidCategory);

        when(categoryService.isCategoryInvalid(invalidCategory)).thenReturn(true);

        InvalidCategoryException exception = assertThrows(
                InvalidCategoryException.class,
                () -> expenseService.addExpense(expenseRequest)
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
        assertEquals(mockExpense.getComment(), response.getComment());
        assertEquals(mockExpense.getCategory().getName().name(), response.getCategory());
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

}
