package com.kbc.kibi_coins.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "processed_statements")
@Data
@NoArgsConstructor
public class Statement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String checksum;

    public Statement(String checksum) {
        this.checksum = checksum;
    }
}
