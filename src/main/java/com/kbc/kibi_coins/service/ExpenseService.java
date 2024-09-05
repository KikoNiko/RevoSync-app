package com.kbc.kibi_coins.service;

import com.kbc.kibi_coins.model.Category;
import com.kbc.kibi_coins.model.CategoryEnum;
import com.kbc.kibi_coins.model.Expense;
import com.kbc.kibi_coins.model.dto.ExpenseRequest;
import com.kbc.kibi_coins.model.dto.ExpenseResponse;
import com.kbc.kibi_coins.repository.CategoryRepository;
import com.kbc.kibi_coins.repository.ExpenseRepository;
import com.kbc.kibi_coins.util.InvalidCategoryException;
import com.kbc.kibi_coins.util.Patcher;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

import static com.kbc.kibi_coins.util.ConstantMessages.INVALID_CATEGORY;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public ExpenseResponse addExpense(ExpenseRequest expenseRequest) {
        if (isCategoryInvalid(expenseRequest.getCategory())) {
            throw new InvalidCategoryException(String.format(INVALID_CATEGORY, expenseRequest.getCategory()));
        }

        Category category = categoryRepository
                .findByName(CategoryEnum.valueOf(expenseRequest.getCategory().toUpperCase()));

        Expense toSave = Expense.builder()
                .amount(expenseRequest.getAmount())
                .category(category)
                .date(expenseRequest.getDate())
                .comment(expenseRequest.getComment())
                .build();

        category.getExpenses().add(toSave);

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
                .sorted(Comparator.comparing(Expense::getDate).reversed())
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

    public List<ExpenseResponse> getAllByCategory(String category) {
        if (isCategoryInvalid(category)) {
            throw new InvalidCategoryException(String.format(INVALID_CATEGORY, category));
        }
        return categoryRepository.findByName(CategoryEnum.valueOf(category.toUpperCase()))
                .getExpenses()
                .stream()
                .map(this::mapToResponse)
                .sorted(Comparator.comparing(ExpenseResponse::getDate).reversed())
                .collect(Collectors.toList());
    }

    static boolean isCategoryInvalid(String categoryName) {
        return !Arrays.stream(CategoryEnum.values())
                .map(Enum::toString)
                .toList()
                .contains(categoryName.toUpperCase());
    }

    public ExpenseResponse updateExpenseByFields(Long id, Expense incompleteExpense) {
        Optional<Expense> byId = expenseRepository.findById(id);
        if (byId.isEmpty()) {
            throw new EntityNotFoundException();
        }
        Expense exp = byId.get();
        try {
            Patcher.expensePatcher(exp, incompleteExpense);
            expenseRepository.save(exp);
            log.info("Expense data updated");
        }catch (Exception e){
            log.error("Oops! Something went wrong...", e);
        }
        return mapToResponse(exp);
    }
}
