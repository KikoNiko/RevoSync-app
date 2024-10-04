package com.kbc.kibi_coins.controller;

import com.kbc.kibi_coins.model.Expense;
import com.kbc.kibi_coins.model.dto.ExpenseRequest;
import com.kbc.kibi_coins.model.dto.ExpenseResponse;
import com.kbc.kibi_coins.service.ExpenseService;
import com.kbc.kibi_coins.util.ConstantMessages;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse addExpense(@Valid @RequestBody ExpenseRequest expenseRequest) {
        return expenseService.addExpense(expenseRequest);
    }

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<Integer> uploadExpenses(@RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(expenseService.uploadExpenses(file));
    }

    @GetMapping("/all")
    public List<ExpenseResponse> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    @GetMapping("/{id}")
    public ExpenseResponse getById(@PathVariable Long id) {
        return expenseService.getExpenseById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpenseById(id);
        return ResponseEntity.ok(String.format(ConstantMessages.EXPENSE_DELETED, id));
    }

    @PatchMapping("/{id}")
    public ExpenseResponse updateExpense(@PathVariable Long id,
                                         @Valid @RequestBody Expense incompleteExpense) {
        return expenseService.updateExpenseByFields(id, incompleteExpense);
    }

    @GetMapping()
    public List<ExpenseResponse> getAllByCategory(@RequestParam(value = "cat") String category) {
        return expenseService.getAllByCategory(category);
    }

    @GetMapping("/{year}/{month}")
    public List<ExpenseResponse> getAllByYearAndMonth(@PathVariable int year, @PathVariable short month) {
        return expenseService.getAllByYearAndMonth(year, month);
    }
}
