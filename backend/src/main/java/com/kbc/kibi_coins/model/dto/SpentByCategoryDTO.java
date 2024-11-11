package com.kbc.kibi_coins.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpentByCategoryDTO {
    private String category;
    private BigDecimal moneySpent;
}
