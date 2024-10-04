package com.kbc.kibi_coins.model.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseCsvRepresentation {
    @CsvBindByName(column = "Started Date")
    private String date;
    @CsvBindByName(column = "Description")
    private String description;
    @CsvBindByName(column = "Amount")
    private String amount;
}
