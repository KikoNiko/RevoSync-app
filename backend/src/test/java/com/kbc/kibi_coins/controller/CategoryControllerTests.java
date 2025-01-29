package com.kbc.kibi_coins.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.kbc.kibi_coins.model.Category;
import com.kbc.kibi_coins.model.CategoryEnum;
import com.kbc.kibi_coins.model.dto.CategoryDto;
import com.kbc.kibi_coins.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

public class CategoryControllerTests {
    private MockMvc mockMvc;
    @Mock
    private CategoryService categoryService;
    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }

    @Test
    void getAllCategories_ShouldReturnListOfCategories() throws Exception {
        List<CategoryDto> categories = Arrays.asList(new CategoryDto("Category1"), new CategoryDto("Category2"));
        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void getCategory_Found_ShouldReturnCategory() throws Exception {
        Category category = new Category(CategoryEnum.OTHERS);
        when(categoryService.getCategoryFromDescription("test")).thenReturn(category);

        mockMvc.perform(get("/api/categories/lookup").param("input", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("OTHERS"));
    }

    @Test
    void getCategory_NotFound_ShouldReturnNotFound() throws Exception {
        when(categoryService.getCategoryFromDescription("unknown")).thenReturn(null);

        mockMvc.perform(get("/api/categories/lookup").param("input", "unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addOrUpdateMapping_Success_ShouldReturnOk() throws Exception {
        when(categoryService.addOrUpdateMapping("comment1", "category1")).thenReturn(true);

        mockMvc.perform(post("/api/categories/add")
                        .param("comment", "comment1")
                        .param("category", "category1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Mapping added successfully."));
    }

    @Test
    void addOrUpdateMapping_Failure_ShouldReturnBadRequest() throws Exception {
        when(categoryService.addOrUpdateMapping("comment1", "category1")).thenReturn(false);

        mockMvc.perform(post("/api/categories/add")
                        .param("comment", "comment1")
                        .param("category", "category1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Mapping for this comment already exists."));
    }

    @Test
    void categorizeCommentsInBatch_ShouldReturnOk() throws Exception {
        doNothing().when(categoryService).categorizeCommentsInBatch();

        mockMvc.perform(post("/api/categories/batchCategorize"))
                .andExpect(status().isOk())
                .andExpect(content().string("Batch categorization complete."));
    }
}
