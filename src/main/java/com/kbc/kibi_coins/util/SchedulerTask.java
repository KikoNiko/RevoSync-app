package com.kbc.kibi_coins.util;

import com.kbc.kibi_coins.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class SchedulerTask {
    private final Logger log = LoggerFactory.getLogger(SchedulerTask.class);
    private final CategoryService categoryService;

    @Scheduled(cron = "0 * * * * *")
    void addExpensesToParentCategories() {
        categoryService.addExpensesFromSubCategoriesToParents();
        log.info("Expenses added to categories at " + Instant.now());
    }
}
