package com.kbc.kibi_coins.model.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseRequest {
    private BigDecimal amount;
    private String category;
    private LocalDate date;
    private String comment;
}
