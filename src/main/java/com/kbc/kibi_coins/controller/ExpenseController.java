package com.kbc.kibi_coins.controller;

import com.kbc.kibi_coins.model.dto.ExpenseRequest;
import com.kbc.kibi_coins.model.dto.ExpenseResponse;
import com.kbc.kibi_coins.service.ExpenseService;
import com.kbc.kibi_coins.util.ConstantMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse addExpense(@RequestBody ExpenseRequest expenseRequest) {
        return expenseService.addExpense(expenseRequest);
    }

    @GetMapping("/{id}")
    public ExpenseResponse getById(@PathVariable Long id) {
        return expenseService.getExpenseById(id);
    }

    @GetMapping("/all")
    public List<ExpenseResponse> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpenseById(id);
        return ResponseEntity.ok(String.format(ConstantMessages.EXPENSE_DELETED, id));
    }
}
