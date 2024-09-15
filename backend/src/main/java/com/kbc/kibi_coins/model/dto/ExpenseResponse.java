package com.kbc.kibi_coins.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class ExpenseResponse {
    private Long id;
    private BigDecimal amount;
    private String category;
    private LocalDate date;
    private String comment;
}
