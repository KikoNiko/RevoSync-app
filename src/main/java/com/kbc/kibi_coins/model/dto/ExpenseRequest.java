package com.kbc.kibi_coins.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseRequest {
    @Positive(message = "Amount must be a positive number!")
    @NotNull(message = "Amount cannot be empty!")
    private BigDecimal amount;
    @NotBlank(message = "Category cannot be empty!")
    private String category;
    @PastOrPresent(message = "Date cannot be in the future!")
    private LocalDate date;
    private String comment;
}
