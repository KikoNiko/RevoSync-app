package com.kbc.kibi_coins.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.kbc.kibi_coins.model.dto.SpentByCategoryDTO;
import com.kbc.kibi_coins.model.dto.SpentByMonthDTO;
import com.kbc.kibi_coins.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

class StatisticsControllerTests {

    private MockMvc mockMvc;

    @Mock
    private StatisticsService statisticsService;

    @InjectMocks
    private StatisticsController statisticsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(statisticsController).build();
    }

    @Test
    void getSpentByMonth_ShouldReturnAmount() throws Exception {
        when(statisticsService.spentByMonth(2023, 5)).thenReturn(BigDecimal.valueOf(100.50));

        mockMvc.perform(get("/api/statistics/spent-by-month").param("year", "2023").param("month", "5"))
                .andExpect(status().isOk())
                .andExpect(content().string("100.5"));
    }

    @Test
    void getSpentByYear_ShouldReturnAmount() throws Exception {
        when(statisticsService.spentByYear(2023)).thenReturn(BigDecimal.valueOf(1200.75));

        mockMvc.perform(get("/api/statistics/spent-by-year").param("year", "2023"))
                .andExpect(status().isOk())
                .andExpect(content().string("1200.75"));
    }

    @Test
    void getSpentByCategory_ShouldReturnAmount() throws Exception {
        when(statisticsService.spentByCategory("food")).thenReturn(BigDecimal.valueOf(250.00));

        mockMvc.perform(get("/api/statistics/spent-by-category").param("name", "food"))
                .andExpect(status().isOk())
                .andExpect(content().string("250.0"));
    }

    @Test
    void getAllSpentByAllMonths_ShouldReturnList() throws Exception {
        List<SpentByMonthDTO> data = List.of(new SpentByMonthDTO("5", BigDecimal.valueOf(500.00)));
        when(statisticsService.getAllSpentByAllMonths()).thenReturn(data);

        mockMvc.perform(get("/api/statistics/all-spent-by-month"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getAllSpentByCategories_ShouldReturnList() throws Exception {
        List<SpentByCategoryDTO> data = List.of(new SpentByCategoryDTO("Food", BigDecimal.valueOf(300.00)));
        when(statisticsService.getAllSpentByCategories()).thenReturn(data);

        mockMvc.perform(get("/api/statistics/all-spent-by-category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }
}