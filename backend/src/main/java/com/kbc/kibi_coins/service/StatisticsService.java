package com.kbc.kibi_coins.service;

import com.kbc.kibi_coins.model.Category;
import com.kbc.kibi_coins.model.CategoryEnum;
import com.kbc.kibi_coins.model.Expense;
import com.kbc.kibi_coins.model.dto.SpentByCategoryDTO;
import com.kbc.kibi_coins.model.dto.SpentByMonthDTO;
import com.kbc.kibi_coins.repository.CategoryRepository;
import com.kbc.kibi_coins.repository.ExpenseRepository;
import com.kbc.kibi_coins.util.InvalidCategoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import static com.kbc.kibi_coins.util.ConstantMessages.INVALID_CATEGORY;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    public BigDecimal spentByMonth(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
        return expenseRepository.sumExpensesInDateRange(startDate, endDate);
    }

    public BigDecimal spentByYear(int year) {
        return expenseRepository.sumExpensesByYear(year);
    }

    public BigDecimal spentByCategory(String category) {
        if (categoryService.isCategoryInvalid(category)) {
            throw new InvalidCategoryException(String.format(INVALID_CATEGORY, category));
        }

        BigDecimal result = expenseRepository
                .getAllByCategory_NameOrderByDateDesc(CategoryEnum.valueOf(category.toUpperCase()))
                .stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (category.equalsIgnoreCase("food")) {
            BigDecimal groceries = spentByCategory("groceries");
            BigDecimal fastFood = spentByCategory("fast_food");
            BigDecimal restaurants = spentByCategory("restaurants");
            return result.add(groceries).add(fastFood).add(restaurants);
        }

        return result;
    }

    public List<SpentByMonthDTO> getAllSpentByAllMonths() {
        List<SpentByMonthDTO> allSpentByMonthData = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();
        short[] months = new short[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        for (short m : months) {
            String monthName = mapMonthFromNumberToString(m);
            SpentByMonthDTO dto =
                    new SpentByMonthDTO(monthName, expenseRepository.sumExpensesByMonth(currentYear, m));
            allSpentByMonthData.add(dto);
        }

        return allSpentByMonthData;
    }

    private String mapMonthFromNumberToString(short monthNumber) {
        if (monthNumber < 1 || monthNumber > 12) {
            return "Not a valid month number!";
        }
        return switch (monthNumber) {
            case 1 -> "January";
            case 2 -> "February";
            case 3 -> "March";
            case 4 -> "April";
            case 5 -> "May";
            case 6 -> "June";
            case 7 -> "July";
            case 8 -> "August";
            case 9 -> "September";
            case 10 -> "October";
            case 11 -> "November";
            case 12 -> "December";
            default -> "";
        };
    }

    public List<SpentByCategoryDTO> getAllSpentByCategories() {
        List<SpentByCategoryDTO> allSpentByCategoriesData = new ArrayList<>();
        List<Category> allCategories = categoryRepository.findAll();
        allCategories.forEach(cat -> {
            SpentByCategoryDTO dto =
                    new SpentByCategoryDTO(
                            String.valueOf(cat.getName()),
                            spentByCategory(String.valueOf(cat.getName())));
            allSpentByCategoriesData.add(dto);
        });

        return allSpentByCategoriesData;
    }
}
