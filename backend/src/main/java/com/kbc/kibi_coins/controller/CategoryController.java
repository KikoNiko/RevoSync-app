package com.kbc.kibi_coins.controller;

import com.kbc.kibi_coins.model.Category;
import com.kbc.kibi_coins.model.dto.CategoryDto;
import com.kbc.kibi_coins.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping()
    public List<CategoryDto> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/lookup")
    public ResponseEntity<Category> getCategory(@RequestParam String input) {
        Category category = categoryService.getCategoryFromDescription(input);
        if (category != null) {
            return ResponseEntity.ok(category);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addOrUpdateMapping(@RequestParam String comment, @RequestParam String category) {
        boolean added = categoryService.addOrUpdateMapping(comment, category);
        if (added) {
            return ResponseEntity.ok("Mapping added successfully.");
        } else {
            return ResponseEntity.badRequest().body("Mapping for this comment already exists.");
        }
    }

    @PostMapping("/batchCategorize")
    public ResponseEntity<String> categorizeCommentsInBatch() {
        categoryService.categorizeCommentsInBatch();
        return ResponseEntity.ok("Batch categorization complete.");
    }
}
