package com.kbc.kibi_coins.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbc.kibi_coins.model.Category;
import com.kbc.kibi_coins.model.CategoryEnum;
import com.kbc.kibi_coins.model.Expense;
import com.kbc.kibi_coins.model.dto.ExpenseRequest;
import com.kbc.kibi_coins.model.dto.ExpenseResponse;
import com.kbc.kibi_coins.service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ExpenseController.class)
@ExtendWith(MockitoExtension.class)
public class ExpenseControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ExpenseService expenseService;
    @Autowired
    private ObjectMapper objectMapper;

    private Expense testExpense;
    private ExpenseRequest expenseRequest;

    private ExpenseResponse expenseResponse;

    @BeforeEach
    void setUp() {
        Category testCategory = new Category(CategoryEnum.EDUCATION);

        testExpense = Expense.builder()
                .id(1L)
                .amount(BigDecimal.TEN)
                .category(testCategory)
                .date(LocalDate.now())
                .comment("this is a test expense")
                .build();

        expenseRequest = ExpenseRequest.builder()
                .amount(testExpense.getAmount())
                .category(testExpense.getCategory().toString())
                .date(testExpense.getDate())
                .comment(testExpense.getComment())
                .build();

        expenseResponse = ExpenseResponse.builder()
                .id(testExpense.getId())
                .amount(testExpense.getAmount())
                .category(testExpense.getCategory().toString())
                .date(testExpense.getDate())
                .comment(testExpense.getComment())
                .build();
    }

    @Test
    void addExpense_ShouldReturnCreated() throws Exception {
        when(expenseService.addExpense(expenseRequest)).thenReturn(expenseResponse);

        MvcResult result = mockMvc.perform(post("/api/expenses/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        ExpenseResponse resultResponse = objectMapper.readValue(contentAsString, ExpenseResponse.class);
        assertThat(resultResponse).isEqualTo(expenseResponse);
        assertThat(resultResponse.getId()).isEqualTo(testExpense.getId());

    }

    @Test
    void getExpenseById_ShouldReturn() throws Exception {
        when(expenseService.getExpenseById(1L)).thenReturn(expenseResponse);
        MvcResult mvcResult = mockMvc.perform(get("/api/expenses/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ExpenseResponse resultResponse = objectMapper.readValue(contentAsString, ExpenseResponse.class);
        assertThat(resultResponse).isEqualTo(expenseResponse);
    }

}
