package com.kbc.kibi_coins.service;

import com.kbc.kibi_coins.model.Category;
import com.kbc.kibi_coins.model.CategoryEnum;
import com.kbc.kibi_coins.model.Expense;
import com.kbc.kibi_coins.model.dto.ExpenseRequest;
import com.kbc.kibi_coins.model.dto.ExpenseResponse;
import com.kbc.kibi_coins.repository.CategoryRepository;
import com.kbc.kibi_coins.repository.ExpenseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public ExpenseResponse addExpense(ExpenseRequest expenseRequest) {
        Category category = categoryRepository
                .findByName(CategoryEnum.valueOf(expenseRequest.getCategory()))
                .orElseThrow(IllegalArgumentException::new);

        Expense toSave = Expense.builder()
                .amount(expenseRequest.getAmount())
                .category(category)
                .date(expenseRequest.getDate())
                .comment(expenseRequest.getComment())
                .build();

        expenseRepository.save(toSave);
        log.info("New Expense Saved!");
        return ExpenseResponse.builder()
                .id(toSave.getId())
                .amount(toSave.getAmount())
                .category(expenseRequest.getCategory())
                .date(toSave.getDate())
                .comment(toSave.getComment())
                .build();
    }

    public ExpenseResponse getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return mapToResponse(expense);
    }

    public List<ExpenseResponse> getAllExpenses() {
        return expenseRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void deleteExpenseById(Long id) {
        Expense expense = expenseRepository
                .findById(id)
                .orElseThrow(EntityNotFoundException::new);
        expenseRepository.delete(expense);
    }

    private ExpenseResponse mapToResponse(Expense exp) {
        return ExpenseResponse.builder()
                .id(exp.getId())
                .amount(exp.getAmount())
                .category(exp.getCategory().getName().toString())
                .date(exp.getDate())
                .comment(exp.getComment())
                .build();
    }

}
