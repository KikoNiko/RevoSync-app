package com.kbc.kibi_coins.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbc.kibi_coins.model.Category;
import com.kbc.kibi_coins.model.CategoryEnum;
import com.kbc.kibi_coins.model.Expense;
import com.kbc.kibi_coins.model.dto.ExpenseRequest;
import com.kbc.kibi_coins.model.dto.ExpenseResponse;
import com.kbc.kibi_coins.service.ExpenseService;
import com.kbc.kibi_coins.util.ConstantMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ExpenseController.class)
@WithMockUser
public class ExpenseControllerTests {
    @MockBean
    SecurityFilterChain securityFilterChain;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ExpenseService expenseService;
    @Autowired
    private ObjectMapper objectMapper;

    private ExpenseRequest expenseRequest;

    private ExpenseResponse expenseResponse;

    @BeforeEach
    void setUp() {
        Category testCategory = new Category(CategoryEnum.EDUCATION);

        Expense testExpense = Expense.builder()
                .id(1L)
                .amount(BigDecimal.TEN)
                .category(testCategory)
                .date(LocalDate.now())
                .comment("this is a test expense")
                .build();

        expenseRequest = ExpenseRequest.builder()
                .amount(testExpense.getAmount())
                .category(testCategory.toString())
                .date(testExpense.getDate())
                .comment(testExpense.getComment())
                .build();

        expenseResponse = ExpenseResponse.builder()
                .id(testExpense.getId())
                .amount(testExpense.getAmount())
                .category(testCategory.toString())
                .date(testExpense.getDate())
                .comment(testExpense.getComment())
                .build();
    }

    @Test
    void addExpense_ShouldReturnCreated() throws Exception {
        when(expenseService.addExpense(expenseRequest)).thenReturn(expenseResponse);

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(expenseRequest)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    void getExpenseById_ShouldReturn() throws Exception {
        when(expenseService.getExpenseById(1L)).thenReturn(expenseResponse);
        mockMvc.perform(get("/api/expenses/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void getAllExpenses() throws Exception {
        when(expenseService.getAllExpenses()).thenReturn(List.of(expenseResponse));
        mockMvc.perform(get("/api/expenses/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void updateExpense_ShouldReturnUpdatedExpense() throws Exception {
        Long expenseId = 1L;
        ExpenseRequest request = new ExpenseRequest(BigDecimal.valueOf(200.00), expenseRequest.getCategory(), LocalDate.now(), "Updated comment");
        ExpenseResponse response = new ExpenseResponse(expenseId, BigDecimal.valueOf(200.00), expenseRequest.getCategory(), LocalDate.now(), "Updated comment");

        when(expenseService.updateExpenseByFields(eq(expenseId), any(ExpenseRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/expenses/{id}", expenseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expenseId))
                .andExpect(jsonPath("$.comment").value("Updated comment"))
                .andExpect(jsonPath("$.amount").value(200.00));
    }

    @Test
    void deleteExpense_ShouldReturnOk() throws Exception {
        Long expenseId = 1L;
        doNothing().when(expenseService).deleteExpenseById(expenseId);

        mockMvc.perform(delete("/api/expenses/{id}", expenseId))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format(ConstantMessages.EXPENSE_DELETED, expenseId)));
    }
}
