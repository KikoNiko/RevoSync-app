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
    @Positive
    @NotNull
    private BigDecimal amount;
    @NotBlank
    private String category;
    @PastOrPresent
    private LocalDate date;
    private String comment;
}
