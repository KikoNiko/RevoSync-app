package com.kbc.kibi_coins.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

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
    @NotBlank
    private CategoryEnum name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;
    @OneToMany(mappedBy = "parentCategory")
    private Set<Category> subCategories = new HashSet<>();

    public Category() {
    }

    public Category(CategoryEnum name) {
        this.name = name;
    }

    public Category addSubCategory(CategoryEnum subCategoryName) {
        Category sub = new Category();
        sub.setName(subCategoryName);
        this.subCategories.add(sub);
        sub.setParentCategory(this);
        return sub;
    }

    public void moveCategory(Category newParent) {
        this.getParentCategory().getSubCategories().remove(this);
        this.setParentCategory(newParent);
        newParent.getSubCategories().add(this);
    }

}
