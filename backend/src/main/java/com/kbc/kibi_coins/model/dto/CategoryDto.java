package com.kbc.kibi_coins.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDto {
    private Long id;
    private String name;

    public CategoryDto() {
    }

    public CategoryDto(String name) {
        this.name = name;
    }
}
