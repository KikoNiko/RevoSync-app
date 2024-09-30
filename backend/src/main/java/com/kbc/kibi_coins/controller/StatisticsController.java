package com.kbc.kibi_coins.controller;

import com.kbc.kibi_coins.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/spent-this-month")
    public ResponseEntity<BigDecimal> getSpentThisMonth(@RequestParam int year, @RequestParam int month) {
        BigDecimal amountSpent = statisticsService.spentByMonth(year, month);
        return ResponseEntity.ok(amountSpent);
    }

    @GetMapping("spent-by-year")
    public BigDecimal getSpentByYear(@RequestParam int year) {
        return statisticsService.spentThisYear(year);
    }

    @GetMapping("/cat")
    public BigDecimal getSpentByCategory(@RequestParam(value = "name") String category) {
        return statisticsService.spentByCategory(category);
    }
}
