package com.kbc.kibi_coins.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ExpenseResponse {
    private Long id;
    private BigDecimal amount;
    private String category;
    private LocalDate date;
    private String comment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpenseResponse that = (ExpenseResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(amount, that.amount) && Objects.equals(category, that.category) && Objects.equals(date, that.date) && Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, category, date, comment);
    }
}
