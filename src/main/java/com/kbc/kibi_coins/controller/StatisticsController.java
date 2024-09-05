package com.kbc.kibi_coins.controller;

import com.kbc.kibi_coins.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/{year}")
    public BigDecimal getSpentByMonth(@PathVariable int year, @RequestParam(value = "month") short month) {
        return statisticsService.spentThisMonth(year, month);
    }

    @GetMapping
    public BigDecimal getSpentByYear(@RequestParam int year) {
        return statisticsService.spentThisYear(year);
    }

    @GetMapping("/cat")
    public BigDecimal getSpentByCategory(@RequestParam(value = "name") String category) {
        return statisticsService.spentByCategory(category);
    }
}
