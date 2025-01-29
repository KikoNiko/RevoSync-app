package com.kbc.kibi_coins.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;
    @Enumerated(EnumType.STRING)
    @NotNull
    private CategoryEnum name;

    public Category() {
    }

    public Category(CategoryEnum name) {
        this.name = name;
    }

}
