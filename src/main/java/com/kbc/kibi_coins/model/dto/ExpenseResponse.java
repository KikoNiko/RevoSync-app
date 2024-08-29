package com.kbc.kibi_coins.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ExpenseResponse {
    private Long id;
    private BigDecimal amount;
    private String category;
    private String comment;
}
