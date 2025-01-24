package com.kbc.kibi_coins.service;

import com.kbc.kibi_coins.model.Category;
import com.kbc.kibi_coins.model.CategoryEnum;
import com.kbc.kibi_coins.model.Expense;
import com.kbc.kibi_coins.model.dto.ExpenseCsvRepresentation;
import com.kbc.kibi_coins.model.dto.ExpenseRequest;
import com.kbc.kibi_coins.model.dto.ExpenseResponse;
import com.kbc.kibi_coins.repository.CategoryRepository;
import com.kbc.kibi_coins.repository.ExpenseRepository;
import com.kbc.kibi_coins.util.InvalidCategoryException;
import com.kbc.kibi_coins.util.Patcher;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.kbc.kibi_coins.util.ConstantMessages.INVALID_CATEGORY;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final StatementService statementService;
    private final CategoryService categoryService;

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
        if (exp.getCategory() == null) {
            return ExpenseResponse.builder()
                    .id(exp.getId())
                    .amount(exp.getAmount())
                    .date(exp.getDate())
                    .comment(exp.getComment())
                    .build();
        }
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
        return expenseRepository
                .getAllByCategory_NameOrderByDateDesc(CategoryEnum.valueOf(category.toUpperCase()))
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

    public ExpenseResponse updateExpenseByFields(Long id, ExpenseRequest incompleteExpense) {
        Optional<Expense> byId = expenseRepository.findById(id);
        if (byId.isEmpty()) {
            throw new EntityNotFoundException();
        }
        if (!incompleteExpense.getCategory().isEmpty()) {
            String categoryString = incompleteExpense.getCategory();
            if (categoryRepository.findByName(CategoryEnum.valueOf(categoryString)) == null) {
                throw new InvalidCategoryException(String.format(INVALID_CATEGORY, categoryString));
            }
        }
        Expense toUpdate = mapToExpense(incompleteExpense);
        Expense exp = byId.get();
        try {
            Patcher.expensePatcher(exp, toUpdate);
            expenseRepository.save(exp);
            log.info("Expense data updated");
        }catch (Exception e){
            log.error("Oops! Something went wrong...", e);
        }
        return mapToResponse(exp);
    }

    private Expense mapToExpense(ExpenseRequest expenseRequest) {
        return Expense.builder()
                .amount(expenseRequest.getAmount())
                .category(categoryRepository.findByName(CategoryEnum.valueOf(expenseRequest.getCategory())))
                .date(expenseRequest.getDate())
                .comment(expenseRequest.getComment())
                .build();
    }

    public List<ExpenseResponse> getAllByYearAndMonth(int year, short month) {
        return expenseRepository.findAllByDate_Month(year, month)
                .stream()
                .map(this::mapToResponse)
                .sorted(Comparator.comparing(ExpenseResponse::getDate).reversed())
                .collect(Collectors.toList());
    }

    public Integer uploadExpenses(MultipartFile file) throws IOException {
        String fileChecksum = statementService.getFileChecksum(file);
        if (statementService.isFileProcessed(fileChecksum)) {
            return -1;
        }

        List<Expense> expenses = parseCsv(file);
        expenseRepository.saveAll(expenses);
        statementService.saveFileMetadata(fileChecksum);
        return expenses.size();
    }

    private List<Expense> parseCsv(MultipartFile file) throws IOException {
        try(Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            HeaderColumnNameMappingStrategy<ExpenseCsvRepresentation> strategy
                    = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(ExpenseCsvRepresentation.class);

            CsvToBean<ExpenseCsvRepresentation> csvToBean
                    = new CsvToBeanBuilder<ExpenseCsvRepresentation>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreEmptyLine(true)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse()
                    .stream()
                    .map(csvLine -> {
                        BigDecimal expenseAmount = verifyExpenseAmount(csvLine.getAmount());
                        boolean isValidDescription = verifyDescription(csvLine.getDescription());
                        if (expenseAmount != null && isValidDescription) {
                            return Expense.builder()
                                    .date(parseDate(csvLine.getDate()))
                                    .comment(csvLine.getDescription())
                                    .amount(expenseAmount)
                                    .category(categoryService.getCategoryFromDescription(csvLine.getDescription()))
                                    .build();
                        }
                        return null;
                    })
                    .filter(Objects::nonNull).toList();
        }
    }

    private LocalDate parseDate(String date) {
        String localDate = date.split("\\s+")[0];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(localDate, formatter);
    }

    private BigDecimal verifyExpenseAmount(String amount) {
        if (amount.startsWith("-")) {
            return new BigDecimal(amount.substring(1));
        } else {
            return null;
        }
    }

    private boolean verifyDescription(String description) {
        return !description.contains("Exchanged to");
    }

    public int categorizeExpenses() {
        int count = 0;
        for (Expense e : expenseRepository.findAll()) {
            e.setCategory(
                    categoryService.getCategoryFromDescription(e.getComment())
            );
            expenseRepository.save(e);
            count ++;
        }
        return count;
    }
}
