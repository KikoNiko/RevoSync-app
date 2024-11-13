package com.kbc.kibi_coins.service;

import com.kbc.kibi_coins.model.Category;
import com.kbc.kibi_coins.model.CategoryEnum;
import com.kbc.kibi_coins.model.CommentCategoryMapping;
import com.kbc.kibi_coins.model.dto.CategoryDto;
import com.kbc.kibi_coins.repository.CategoryRepository;
import com.kbc.kibi_coins.repository.CommentCategoryMappingRepository;
import com.kbc.kibi_coins.repository.ExpenseRepository;
import com.kbc.kibi_coins.util.CategoryMapping;
import com.kbc.kibi_coins.util.ConstantMessages;
import com.kbc.kibi_coins.util.InvalidCategoryException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CommentCategoryMappingRepository ccmRepository;
    private final ExpenseRepository expenseRepository;
    private final CategoryMapping categoryMapping;


    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private CategoryDto map(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getCategoryId());
        categoryDto.setName(category.getName().name());
        return categoryDto;
    }

    public Category getCategoryFromDescription(String comment) {
        return categoryMapping.getCategoryMappings().getOrDefault(comment, null);
    }

    @PostConstruct
    public void initCategoryMappings() {
        ccmRepository
                .findAll()
                .forEach(m -> {
                    Category category = categoryRepository.findByName(CategoryEnum.valueOf(m.getCategoryName()));
                    categoryMapping.getCategoryMappings().put(m.getComment(), category);
                });
    }

    public boolean addOrUpdateMapping(String comment, String categoryName) {
        Category category = categoryRepository.findByName(CategoryEnum.valueOf(categoryName.toUpperCase()));
        if (category == null) {
            throw new InvalidCategoryException(String.format(ConstantMessages.INVALID_CATEGORY, categoryName));
        }

        CommentCategoryMapping existingMapping = ccmRepository.findByComment(comment).orElse(null);

        if (existingMapping != null) {
            existingMapping.setCategoryName(categoryName);
            ccmRepository.save(existingMapping);
        } else {
            CommentCategoryMapping newMapping = new CommentCategoryMapping();
            newMapping.setComment(comment);
            newMapping.setCategoryName(categoryName);
            ccmRepository.save(newMapping);
        }

        categoryMapping.getCategoryMappings().put(comment, category);
        return true;
    }

    public void categorizeCommentsInBatch() {
        List<String> commentsToCategorize = getAllComments();

        for (String comment : commentsToCategorize) {
            String category = determineCategory(comment);
            addOrUpdateMapping(comment, category);
        }
    }

    private List<String> getAllComments() {
        return expenseRepository.findAllDistinctComments();
    }

    private String determineCategory(String comment) {
        List<String> transportationList = categoryMapping.getTransportationList();
        List<String> foodList = categoryMapping.getFoodList();
        List<String> entertainmentList = categoryMapping.getEntertainmentList();
        List<String> healthcareList = categoryMapping.getHealthcareList();
        List<String> clothingList = categoryMapping.getClothingList();
        List<String> educationList = categoryMapping.getEducationList();
        List<String> suppliesList = categoryMapping.getSuppliesList();
        List<String> sportsList = categoryMapping.getSportsList();

        if (transportationList.contains(comment)) {
            return "TRANSPORTATION";
        }
        if (foodList.contains(comment)) {
            return "FOOD";
        }
        if (entertainmentList.contains(comment)) {
            return "ENTERTAINMENT";
        }
        if (comment.toLowerCase().contains("pharmacy") || healthcareList.contains(comment)) {
            return "HEALTHCARE";
        }
        if (clothingList.contains(comment)) {
            return "CLOTHING";
        }
        if (educationList.contains(comment)) {
            return "EDUCATION";
        }
        if (suppliesList.contains(comment)) {
            return "HOUSEHOLD_SUPPLIES";
        }
        if (sportsList.contains(comment)) {
            return "SPORTS";
        }

        else return "OTHERS";
    }
}
