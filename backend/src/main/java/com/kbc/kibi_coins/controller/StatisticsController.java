package com.kbc.kibi_coins.controller;

import com.kbc.kibi_coins.model.dto.SpentByCategoryDTO;
import com.kbc.kibi_coins.model.dto.SpentByMonthDTO;
import com.kbc.kibi_coins.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/spent-by-month")
    public ResponseEntity<BigDecimal> getSpentByMonth(@RequestParam int year, @RequestParam int month) {
        BigDecimal amountSpent = statisticsService.spentByMonth(year, month);
        return ResponseEntity.ok(amountSpent);
    }

    @GetMapping("spent-by-year")
    public BigDecimal getSpentByYear(@RequestParam int year) {
        return statisticsService.spentByYear(year);
    }

    @GetMapping("/spent-by-category")
    public BigDecimal getSpentByCategory(@RequestParam(value = "name") String category) {
        return statisticsService.spentByCategory(category);
    }

    @GetMapping("/all-spent-by-month")
    public List<SpentByMonthDTO> getAllSpentByAllMonths() {
        return statisticsService.getAllSpentByAllMonths();
    }

    @GetMapping("/all-spent-by-category")
    public List<SpentByCategoryDTO> getAllSpentByCategories() {
        return statisticsService.getAllSpentByCategories();
    }
}
