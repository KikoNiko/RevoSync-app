package com.kbc.kibi_coins.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class SpentByMonthDTO {
    private String month;
    private BigDecimal moneySpent;

    private boolean isMonthValid(String month) {
        List<String> validMonths = List.of(
                "January",
                "February",
                "March",
                "April",
                "May",
                "June",
                "July",
                "August",
                "September",
                "October",
                "November",
                "December");
        return validMonths.contains(month);
    }

    public SpentByMonthDTO(String month, BigDecimal moneySpent) {
        if (isMonthValid(month)) {
            this.month = month;
            this.moneySpent = moneySpent;
        }
    }
}
